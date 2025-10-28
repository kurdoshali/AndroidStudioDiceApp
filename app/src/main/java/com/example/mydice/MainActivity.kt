package com.example.mydice

import android.R.attr.label
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mydice.ui.theme.MyDiceTheme
import kotlin.math.abs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDiceTheme {
                Dice()
            }
        }
    }
}
@Composable
fun Dice(){
    //Dice Values
    var dieIndex by remember { mutableIntStateOf(5) }
    var dieValueOne by remember {mutableIntStateOf(0)}
    var dieValueTwo by remember {mutableIntStateOf(0)}
    var dieValueThree by remember {mutableIntStateOf(0)}
    var counter by remember {mutableIntStateOf(0)}
    var randomNum by remember {mutableIntStateOf(generateRandomNum())} //random number generator
    var probabilityOfWinning by remember {mutableDoubleStateOf(        getProbabilityOfWinning(randomNum))}
    var cashAmount by remember {mutableIntStateOf(100)}
    var betAmount by remember {mutableIntStateOf(0)}
    var isUserPlaying by remember {mutableStateOf(false)}
    var dieTotal by remember {mutableIntStateOf(0)}
    var potentialCashout by remember{ mutableDoubleStateOf(0.0) }
    var testCheck by remember{ mutableStateOf(false) }


    val dieArray=arrayOf(
        R.drawable.dice1,
        R.drawable.dice2,
        R.drawable.dice3,
        R.drawable.dice4,
        R.drawable.dice5,
        R.drawable.dice6
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        // verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        //TOP INTERFACE
        Text("Cash: $cashAmount")
        Text("Bet Amount: $betAmount")
        //MINUS PLUS ENTER
        Row ()
        {
            Button(onClick={
                if (betAmount != 0) {
                    cashAmount = cashAmount + 10
                    betAmount = betAmount - 10
                } },
                enabled = !isUserPlaying
            ){
                Text("-")
            }

            Button(onClick={
                if (cashAmount > 0) {
                    cashAmount = cashAmount - 10
                    betAmount = betAmount + 10
                } },
                enabled = !isUserPlaying
            ){
                Text("+")
            }
            Button(onClick={
                isUserPlaying = true
//                Log.d("Debug", )
                potentialCashout= calculateCashout(betAmount, probabilityOfWinning)
            },
                enabled = !isUserPlaying
            ){
                Text("Enter")
            }
        }

        //DICE IMAGE
        Spacer(modifier = Modifier.height(24.dp))
        Image(
            painter= painterResource(id=dieArray[dieIndex]),
            contentDescription = "Dice image",
            modifier = Modifier
                .size(150.dp)
        )

        //ROLL DIE BUTTON
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick={
            dieIndex=rollDie()
            when (counter) {
                0 -> {
                    dieValueOne = dieIndex + 1
                }
                1 -> {
                    dieValueTwo = dieIndex + 1
                }
                2 -> {
                    dieValueThree = dieIndex + 1
                    testCheck = true
                }
            }
            counter = (counter+1)
        },
        enabled = isUserPlaying && counter < 3
        )
        { Text("Roll Die")}

        //ROLL ROW
        Spacer(modifier = Modifier.height(24.dp))
        Row(){
            Text("First Roll: $dieValueOne")
            Spacer(Modifier.width(40.dp))
            Text("Second Roll: $dieValueTwo")
            Spacer(Modifier.width(40.dp))
            Text("Third Roll: $dieValueThree")
        }
        dieTotal = dieValueOne + dieValueTwo + dieValueThree
        Text("Total: $dieTotal")

        //Add cashout to cash
        if(counter > 2 && dieTotal>randomNum && testCheck){
            cashAmount += potentialCashout.toInt()
            testCheck = false
        }

        //If Lost Substract Cashout
        if(counter > 2 && dieTotal<=randomNum && testCheck){
            cashAmount -= betAmount
            testCheck = false
        }

        //PLAY AGAIN BUTTON
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                counter = 0
                dieValueOne = 0
                dieValueTwo = 0
                dieValueThree = 0
                randomNum = generateRandomNum()
                probabilityOfWinning = getProbabilityOfWinning(randomNum)
                isUserPlaying = false
            },
            enabled = counter == 3
        ) {
            Text("Play Again")
        }

        Spacer(modifier = Modifier.height(24.dp))
        //BOTTOM INTERFACE
        if(isUserPlaying) {
            //ROLL HIGHER THAN
            Text("Roll Higher Than: $randomNum")

            //PROBABILITY OF WINNING
            Text("Probability of Winning: ${String.format("%.1f", probabilityOfWinning)}%")

            //CALCULATE CASHOUT
            Text("Potential Cashout: $potentialCashout")

            //LOST SCREEN
            if(cashAmount <= 0 && counter > 2){
                Text("GAME OVER")
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun NumberInputField(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = { newValue ->
            // Allow only digits (optional decimal point if you want)
            if (newValue.all { it.isDigit() }) {
                onValueChange(newValue)
            }
        },
        label = { Text("Enter a number") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

fun calculateCashout (betAmount: Int, probabilityOfWinning: Double): Double{
    val temp = probabilityOfWinning / 100
    return betAmount/temp
}

fun generateRandomNum():Int{
    val roll=(3..17).random()
    return roll
}

fun getProbabilityOfWinning(randomNum: Int):Double{
    val probabilities = arrayOf(
        215.0/216.0,
        212.0/216.0,
        206.0/216.0,
        196.0/216.0,
        181.0/216.0,
        160.0/216.0,
        135.0/216.0,
        108.0/216.0,
        81.0/216.0,
        56.0/216.0,
        35.0/216.0,
        20.0/216.0,
        10.0/216.0,
        4.0/216.0,
        1.0/216.0,
    )
    return probabilities[randomNum - 3] * 100
}

fun rollDie():Int{
    val roll=(0..5).random()
    return roll
}

@Preview(showBackground = true)
@Composable
fun DicePreview() {
    MyDiceTheme {
        Dice()
    }
}