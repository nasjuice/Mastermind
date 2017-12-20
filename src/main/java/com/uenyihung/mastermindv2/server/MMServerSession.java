package com.uenyihung.mastermindv2.server;
import com.uenyihung.mastermindv2.datacom.MMPacket;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Description: Creates a server session of MasterMind games.
 *
 * @author Uen Yi Cindy Hung 1031002
 * @since 20/10/2016
 * @version 2.0
 */
public class MMServerSession {
    private final MMPacket clSocket;
    private int numOfGuess;
    private byte[] answer;
    private boolean sessionContinue;
    private boolean gameContinue;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    /**
     * 1 Parameter Constructor.
     *
     * @param clSocket
     * @throws IOException
     */
    public MMServerSession(MMPacket clSocket) throws IOException {
        this.clSocket = clSocket;
    }
    /**
     * Starts the gaming session.
     *
     * @throws IOException
     */
    public void startSession() throws IOException {
        log.info("MMServerSession: startSession");
        sessionContinue = true;
        while (sessionContinue && !clSocket.isClosed()) {
            startGame();
        }
    }
    /**
     * Start one game.
     * 
     * Packets 5 bytes
     * byte 1 : state of the game [0: new game, 1: continue game, 2: end game]
     * byte 2,3,4,5 : the 4 colors, represented by a range from 0 to 7
     *
     * @throws IOException
     */
    private void startGame() throws IOException {
        byte[] msg;
        byte[] guess = new byte[4];
        byte[] response = new byte[5];
        byte[] hint;
        gameContinue = true;
        log.info("MMServerSession: startGame");
        while (gameContinue && !clSocket.isClosed()) {
            log.info("Socket inside loop: " + clSocket.isClosed());
            msg = clSocket.readPacket();
            log.info("MMServerSession: startGame - game start");
            switch (msg[0]) {
                // GAME TERMINATES
                case -1:
                    clSocket.close();
                    response[0] = (byte)-1;
                    response[1] = (byte)-1;
                    response[2] = (byte)-1;
                    response[3] = (byte)-1;
                    response[4] = (byte)-1;
                    break;
                // NEW GAME
                case 0:
                    log.info("MMServerSession: startGame - switch new game");
                    answer = generateAnswer(msg);
                    numOfGuess = 0;
                    // OK READY, client can start game.
                    response[0] = (byte)0;
                    response[1] = (byte)0;
                    response[2] = (byte)0;
                    response[3] = (byte)0;
                    response[4] = (byte)0;
                    break;
                // GAME CONTINUE
                case 1:
                    log.info("MMServerSession: startGame - switch game continue, #of guess: " + numOfGuess);
                    if (numOfGuess < 10) {
                        // Get the user's guess.
                        guess[0] = msg[1];
                        guess[1] = msg[2];
                        guess[2] = msg[3];
                        guess[3] = msg[4];
                        hint = verifyAnswer(guess);
                        if (checkWin(hint) || numOfGuess == 9) {
                            // Guessed the right answer OR no more guesse chances.
                            log.info("MMServerSession: startGame - switch game continue - will end game");
                            response[0] = 2;
                            response[1] = answer[0];
                            response[2] = answer[1];
                            response[3] = answer[2];
                            response[4] = answer[3];
                            gameContinue = false;
                        } else {
                            // Guessed wrong.
                            log.info("MMServerSession: startGame - switch game continue - wrong answer ");
                            response[0] = 1;
                            response[1] = hint[0];
                            response[2] = hint[1];
                            response[3] = hint[2];
                            response[4] = hint[3];
                        }
                        numOfGuess++;
                    }
                    break;
                //GAME END
                case 2:
                    log.info("MMServerSession: startGame - switch end game ");
                    gameContinue = false;
                    response[0] = 2;
                    response[1] = answer[0];
                    response[2] = answer[1];
                    response[3] = answer[2];
                    response[4] = answer[3];
                    break;
                //END SESSION ---------------------- IS THIS NECESSARY?
                default:
                    log.info("MMServerSession: startGame - switch default? ");
                    gameContinue = false;
                    sessionContinue = false;
                    response[0] = 2;
                    response[1] = answer[0];
                    response[2] = answer[1];
                    response[3] = answer[2];
                    response[4] = answer[3];
                    break;
            }
            log.info("MMServerSession: startGame - writing response");
            clSocket.write(response);
        }
    }
    /**
     * Check if the hints reflects a winning combination.
     *
     * @param hints
     * @return TRUE if won else FALSE.
     */
    private boolean checkWin(byte[] hints) {
        log.info("MMServerSession: checkWin");
        boolean won = true;
        for (int i = 0; i < hints.length; i++) {
            if (hints[i] != 0) {
                won = false;
                break;
            }
        }
        return won;
    }
    /**
     * Generates an answer depending on the message given. All 0 will result a
     * random generated answer otherwise, answer is equals to the last 4 bytes
     * of the message.
     *
     * @return
     */
    private byte[] generateAnswer(byte[] msg) {
        log.info("MMServerSession: generateAnswer");
        byte[] generated = new byte[4];
        boolean answerPreSet = false;
        for (int i = 1; i < msg.length; i++) {
            if (msg[i] != 0) {
                answerPreSet = true;
                break;
            }
        }
        if (answerPreSet) {
            log.info("MMServerSession: generateAnswer - answerPreSet");
            for (int i = 0; i < generated.length; i++) {
                generated[i] = msg[i + 1];
            }
        } else {
            log.info("MMServerSession: generateAnswer - not answerPreSet");
            for (int i = 0; i < generated.length; i++) {
                generated[i] = (byte) ((Math.random() * 8)+1);
            }
        }
        return generated;
    }
    /**
     * Verifies the guess made with the answer and return a byte[] hint that
     * will represent how similar is the guess to the answer.
     *
     * 0 : good color, good position.
     * 1 : good color, bad position.
     * 2 : wrong color.
     *
     * @param guess
     * @return
     */
    private byte[] verifyAnswer(byte[] guess) {
        log.info("MMServerSession: verifyAnswer");
        byte[] hint = new byte[4];
        // To keep track which color has already been matched
        // 0: not matched   1: matched
        int[] freeColor = {0, 0, 0, 0};
        for (int i = 0; i < answer.length; i++) {
            if (guess[i] == answer[i]) {
                hint[i] = (byte) 0;
                freeColor[i] = 1;
            } else {
                hint[i] = (byte) 2;
            }
        }
        for (int i = 0; i < hint.length; i++) {
            //verify which hint position depicted not a good guess
            if (hint[i] == (byte) 2) {
                //match the guess at that position with all colors in the answer
                for (int j = 0; j < answer.length; j++) {
                    // if wrong-position-color is in the answer and there is no good-postion-color at the that position
                    if (guess[i] == answer[j]) {
                        // the color has yet been matched for a set hint
                        if (freeColor[j] == 0) {
                            hint[i] = (byte) 1;
                            freeColor[j] = 1;
                            break;
                        }
                    }
                }
            }
        }
        return hint;
    }
}