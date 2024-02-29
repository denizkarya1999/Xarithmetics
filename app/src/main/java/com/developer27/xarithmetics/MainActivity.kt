package com.developer27.xarithmetics

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.developer27.xarithmetics.databinding.ActivityMainBinding
import kotlin.random.Random
import android.app.AlertDialog
import android.media.MediaPlayer

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer

    // Default scores of both players 1 and 2
    private var player1Score = 0;
    private var player2Score = 0;

    // Declare a variable for result
    var arithmeticResult = 0

    // Declare a variable for die
    var Die = 0;

    // Declare current player
    var currentPlayer = "P2"

    // Declare a variable for jackpot
    var jackPot = 5

    // Declare a variable for user playing for Jackpot
    var jackPotMode = false

    // Declare a variable if the user gets doublePoints
    var doublePoints = false

    // Declare a variable to see whether the music plays or not
    var musicPlays = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize MediaPlayer with the audio file
        mediaPlayer = MediaPlayer.create(this, R.raw.gamemusic)

        // Assign default scores to players
        binding.Player1Score.text = player1Score.toString()
        binding.Player2Score.text = player2Score.toString()

        // Assign the jackpot number
        this.binding.JackpotNumber.text = jackPot.toString()

        // Play music when game is started
        mediaPlayer.start()

        // Roll a die when program initialized
        rollDie()

        // Assign current jackpot to the current jackpot label
        // (Reserved for code)

        // Assign current player to the current player label
        // (Reserved for code)

        // Assign problem to the problem label
        // (Reserved for code)

        // Do activity every time user clicks on Roll Die
        binding.RollDieButton.setOnClickListener {
            rollDie()
        }

        // Do activity every time user clicks on Guess
        binding.guessButton.setOnClickListener {
            checkAnswer(this.binding.answer.text.toString(), arithmeticResult, currentPlayer)
        }

        // Do activity when the user clicks on the sound button
        binding.musicButton.setOnClickListener{
            if(musicPlays){
                mediaPlayer.pause()
                this.binding.musicButton.setImageResource(android.R.drawable.ic_lock_silent_mode_off)
                musicPlays = false
            } else {
                mediaPlayer = MediaPlayer.create(this, R.raw.gamemusic)
                mediaPlayer.start()
                this.binding.musicButton.setImageResource(android.R.drawable.ic_lock_silent_mode)
                musicPlays = true
            }
        }
    }

    // Create a function to roll a dice
    fun rollDie() {
        // Randomly generate a die
        Die = Random.nextInt(6) + 1

        if (Die < 4){
            doublePoints = false
            changePlayers(currentPlayer)
            arithmeticResult = arithmeticProblemGenerator(Die)
        } else if (Die == 4) {
            // Check to if you can roll again
            rollAgainforDoublePoints(Die)
        } else if (Die == 5) {
            // If the user lost a turn generate anther
            loseTurn(Die)
        } else if (Die == 6) {
            // Play for Jackpot
            jackpotPlay(Die)
        }
    }

    // Create a function to change players
    fun changePlayers(cp: String) {
        if (cp == "P1") {
            currentPlayer = "P2"
            this.binding.CurrentPlayer.text = currentPlayer
        } else if (cp == "P2") {
            currentPlayer = "P1"
            this.binding.CurrentPlayer.text = currentPlayer
        }
    }

    // Generate an arithmetic problem
    fun arithmeticProblemGenerator(arithmeticCommand: Int): Int {
        // Generate numbers for addition and subtraction
        var number1 = Random.nextInt(100)
        var number2 = Random.nextInt(100)

        // Generate numbers for multiplication
        var number3 = Random.nextInt(21)
        var number4 = Random.nextInt(21)

        // Store the result variable
        var result = 0;

        // Take action based on commands
        if (arithmeticCommand == 1 && number1 >= number2) {
            this.binding.DieImage.setImageResource(R.drawable.dice1)
            result = number1 + number2
            this.binding.problem.text = "$number1 + $number2"
        } else if (arithmeticCommand == 2 && number1 >= number2) {
            this.binding.DieImage.setImageResource(R.drawable.dice2)
            result = number1 - number2
            this.binding.problem.text = "$number1 - $number2"
        } else if (arithmeticCommand == 3 && number3 >= number4) {
            this.binding.DieImage.setImageResource(R.drawable.dice3)
            result = number3 * number4
            this.binding.problem.text = "$number3 * $number4"
        }
        return result;
    }

    fun rollAgainforDoublePoints(arithmeticCommand: Int){
        if (arithmeticCommand == 4) {
            this.binding.DieImage.setImageResource(R.drawable.dice4)
            doublePoints = true

            var number1 = Random.nextInt(100)
            var number2 = Random.nextInt(100)
            var result = number1 + number2
            this.binding.problem.text = "$number1 + $number2"

            arithmeticResult = result
        }
    }

    // Function to check whether the user loses turn or not
    fun loseTurn(arithmeticCommand: Int){
        if (arithmeticCommand == 5) {
            this.binding.DieImage.setImageResource(R.drawable.dice5)
            showMessageBox("Unlucky!", "Unfortunately, you lost your turn")
            rollDie()
        }
    }

    // Function to play for Jackpot
    fun jackpotPlay(arithmeticCommand: Int){
        if (arithmeticCommand == 6) {
            this.binding.DieImage.setImageResource(R.drawable.dice6)
            showMessageBox("Warning!", "You are now playing for Jackpot.")
            jackPotMode = true
            rollDie()
        }
    }

    // Check whether the answer is correct or not
    fun checkAnswer(input : String, result : Int, player : String){
        if (player == "P1"){
            if(input == result.toString()){
                player1Score++
                if(doublePoints)
                    player1Score++
                if(jackPotMode) {
                    player2Score += (jackPot - 5)
                    jackpotReset()
                }
                this.binding.Player1Score.text = player1Score.toString()
                showMessageBox("Correct!", "Congratulations, your answer is correct.")
                rollDie()
                checkReset(player)
            } else {
                showMessageBox("Wrong!", "Unfortunately, your answer for this question was wrong.")
                if(jackPotMode) {
                    jackPot++
                    this.binding.JackpotNumber.text = jackPot.toString()
                }
                rollDie()
            }
        } else if (player == "P2"){
            if(input == result.toString()){
                player2Score++
                if(doublePoints)
                    player2Score++
                if(jackPotMode) {
                    player2Score += (jackPot - 5)
                    jackpotReset()
                }
                this.binding.Player2Score.text = player2Score.toString()
                showMessageBox("Correct!", "Congratulations, your answer is correct.")
                rollDie()
                checkReset(player)
            } else {
                showMessageBox("Wrong!", "Unfortunately, your answer for this question was wrong.")
                if(jackPotMode) {
                    jackPot++
                    this.binding.JackpotNumber.text = jackPot.toString()
                }
                rollDie()
            }
        }
    }

    // Check whether any of these users reached out to 20 points
    fun checkReset(player : String){
        if (player == "P1"){
            if(player1Score >= 20){
                //If the score is greater than 20, reset the game
                showMessageBox("Result", "The winner is player 1.")
                player1Score = 0
                player2Score = 0
                arithmeticResult = 0
                Die = 0;
                currentPlayer = "P1"
                jackPot = 5
                jackPotMode = false
                binding.Player1Score.text = player1Score.toString()
                binding.Player2Score.text = player2Score.toString()
                this.binding.CurrentPlayer.text = currentPlayer
                this.binding.JackpotNumber.text = jackPot.toString()
            }
        } else if (player == "P2"){
            if(player2Score >= 20){
                //If the score is greater than 20, reset the game
                showMessageBox("Result", "The winner is player 2.")
                player1Score = 0
                player2Score = 0
                arithmeticResult = 0
                Die = 0;
                currentPlayer = "P1"
                jackPot = 5
                jackPotMode = false
                binding.Player1Score.text = player1Score.toString()
                binding.Player2Score.text = player2Score.toString()
                this.binding.CurrentPlayer.text = currentPlayer
                this.binding.JackpotNumber.text = jackPot.toString()
            }
        }
    }

    // Function to reset Jackpot
    fun jackpotReset(){
        jackPot = 5
        this.binding.JackpotNumber.text = jackPot.toString()
        jackPotMode = false
    }

    // Show a message box
    fun showMessageBox(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
            // You can perform additional actions if needed when the user clicks OK
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        // Release the MediaPlayer resources when the activity is destroyed
        mediaPlayer.release()
        super.onDestroy()
    }
}