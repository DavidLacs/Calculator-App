package com.example.calculatorapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculatorapp.databinding.ActivityMainBinding
import java.lang.Math.sqrt

class MainActivity : AppCompatActivity(), View.OnClickListener {

    //Initialize viewBinding
    private lateinit var binding : ActivityMainBinding

    var tempText = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Make sure to put SplashScreen code before setContentView
        Thread.sleep(3000)
        installSplashScreen()

        setContentView(R.layout.activity_main)

        //Initialize viewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Number buttons
        val numberButtonList = listOf(
            binding.oneBtn, binding.twoBtn, binding.threeBtn, binding.fourBtn, binding.fiveBtn,
            binding.sixBtn, binding.sevenBtn, binding.eightBtn, binding.nineBtn, binding.zeroBtn,
            binding.decimalBtn
        )

        //Operator buttons
        val operatorButtons = listOf(binding.divideBtn, binding.timesBtn, binding.minusBtn, binding.plusBtn)

        //-------------------------------------------------//
        // Set listeners for number buttons dynamically
        for(numberBtn in numberButtonList) {
            numberBtn.setOnClickListener {
                tempText += (numberBtn.text.toString()) // Get button text
                binding.outputText.text = tempText
            }
        }

        // Operator buttons

        for(operatorBtn in operatorButtons) {
            operatorBtn.setOnClickListener {
                val operator = operatorBtn.text.toString()
                if(tempText.isEmpty() || tempText.last() in "+-/x") { //Check if the previous input is an operator. Cannot select an operator if so
                    Toast.makeText(this, "Invalid Input", Toast.LENGTH_LONG).show()
                } else {
                    tempText += operator
                    binding.outputText.text = tempText
                }
            }
        }

        // ceBtn
        binding.ceBtn.setOnClickListener {
            tempText = ""
            binding.outputText.text = tempText
        }

        // cBtn
        binding.cBtn.setOnClickListener {
            tempText = tempText.dropLast(1)
            binding.outputText.text = tempText
        }

        // Square root button
        binding.squareRootBtn.setOnClickListener {
            if (tempText.isEmpty()) {
                Toast.makeText(this, "Enter a number first", Toast.LENGTH_LONG).show()
            } else {
                tempText = sqrt(tempText.toDouble()).toString()
                binding.outputText.text = tempText
            }
        }

        binding.equalsBtn.setOnClickListener {
            tempText = evaluateExpression(tempText).toString()
            binding.outputText.text = tempText
        }

        //-----------------------------------------------//

        var isCalculatorOn = false // Track the state

        binding.powerBtn.setOnClickListener {
            if (!isCalculatorOn) {
                // Turning on
                Toast.makeText(this, "Turning Calculator On", Toast.LENGTH_LONG).show()
                binding.outputLayout.setBackgroundResource(R.drawable.border)

                // Enable Calculator Buttons
                for(button in numberButtonList) {
                        button.isEnabled = true
                }

                for(button in operatorButtons) {
                    button.isEnabled = true
                }

                binding.ceBtn.isEnabled = true
                binding.cBtn.isEnabled = true
                binding.signBtn.isEnabled = true
                binding.squareRootBtn.isEnabled = true
                binding.equalsBtn.isEnabled = true

            } else {
                // Turning off
                tempText = ""
                binding.outputText.text = tempText
                Toast.makeText(this, "Turning Calculator Off", Toast.LENGTH_LONG).show()
                binding.outputLayout.setBackgroundResource(R.drawable.offstate_layout) // Reset background

                // Disable calculator buttons
                for(button in numberButtonList) {
                    button.isEnabled = false
                }

                for(button in operatorButtons) {
                    button.isEnabled = false
                }
                
                binding.ceBtn.isEnabled = false
                binding.cBtn.isEnabled = false
                binding.signBtn.isEnabled = false
                binding.squareRootBtn.isEnabled = false
                binding.equalsBtn.isEnabled = false
            }
            isCalculatorOn = !isCalculatorOn // Toggle state
        }



        //-------------------------------------------------------------------------------------//
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }



    fun evaluateExpression(expression: String): Double {
        val numbers = mutableListOf<Double>()
        val operators = mutableListOf<Char>()

        var currentNumber = ""

        for (char in expression) {
            when {
                char.isDigit() || char == '.' -> {
                    currentNumber += char  // Build decimal numbers
                }
                char in "+-x/" -> {
                    if (currentNumber.isNotEmpty()) {
                        numbers.add(currentNumber.toDouble())
                        currentNumber = ""
                    }
                    while (operators.isNotEmpty() && precedence(operators.last()) >= precedence(char)) {
                        compute(numbers, operators)
                    }
                    operators.add(char)
                }
            }
        }

        // Add last number
        if (currentNumber.isNotEmpty()) {
            numbers.add(currentNumber.toDouble())
        }

        // Compute remaining operations
        while (operators.isNotEmpty()) {
            compute(numbers, operators)
        }

        return numbers.first()
    }

    fun precedence(op: Char): Int {
        return when (op) {
            '+', '-' -> 1
            'x', '/' -> 2
            else -> 0
        }
    }

    fun compute(numbers: MutableList<Double>, operators: MutableList<Char>) {
        if (numbers.size < 2 || operators.isEmpty()) return

        val b = numbers.removeAt(numbers.lastIndex)  // Get second operand
        val a = numbers.removeAt(numbers.lastIndex)  // Get first operand
        val op = operators.removeAt(operators.lastIndex)  // ✅ Remove from `operators`, not `numbers`

        val result = when (op) {
            '+' -> a + b
            '-' -> a - b
            'x' -> a * b
            '/' -> a / b
            else -> 0.0
        }

        numbers.add(result)  // Push result back to numbers stack
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

}