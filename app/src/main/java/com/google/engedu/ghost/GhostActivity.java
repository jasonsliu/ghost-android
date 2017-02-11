/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private static final String COMPUTER_WIN = "Computer wins :O";
    private static final String USER_WIN = "You win! :D";
    private GhostDictionary mDictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private String mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            mDictionary = new SimpleDictionary(getAssets().open("words.txt"));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        mFragment = "";
        setChallenge(true);
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    public void challenge(View view) {
        TextView gameStatus = (TextView) findViewById(R.id.gameStatus);
        if(mFragment.length() >= 4 && mDictionary.isWord(mFragment)) {
            gameStatus.setText(USER_WIN + " -- is a word.");
            setChallenge(false);
            return;
        }

        String nextWord = mDictionary.getAnyWordStartingWith(mFragment);
        if(nextWord == null) {
            gameStatus.setText(USER_WIN + " -- is not a word fragment.");
            setChallenge(false);
            return;
        }
        gameStatus.setText(COMPUTER_WIN + " -- wrong challenge.");
        setChallenge(false);
    }

    private void computerTurn() {
        TextView gameStatus = (TextView) findViewById(R.id.gameStatus);
        // Do computer turn stuff then make it the user's turn again
        if(mFragment.length() >= 4 && mDictionary.isWord(mFragment)) {
            gameStatus.setText(COMPUTER_WIN + " -- is a word.");
            setChallenge(false);
            return;
        }

        String nextWord = mDictionary.getAnyWordStartingWith(mFragment);
        if(nextWord == null) {
            gameStatus.setText(COMPUTER_WIN + " -- is not a word fragment.");
            setChallenge(false);
            return;
        } else {
            addLetterToFragment(nextWord.toCharArray()[mFragment.length()]);
        }

        userTurn = true;
        gameStatus.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        TextView gameStatus = (TextView)findViewById(R.id.gameStatus);
        char c = (char)event.getUnicodeChar();
        if(!Character.isLetter(c)) {
            return super.onKeyUp(keyCode, event);
        }

        addLetterToFragment(c);
        computerTurn();

        return true;
    }

    private void addLetterToFragment(char c) {
        TextView ghostText = (TextView)findViewById(R.id.ghostText);
        mFragment += Character.toString(c);
        ghostText.setText(mFragment);
        Log.d("Fragment", mFragment);
    }

    private void setChallenge(Boolean set) {
        Button challengeButton = (Button)findViewById(R.id.challenge_button);
        challengeButton.setClickable(set);
    }
}
