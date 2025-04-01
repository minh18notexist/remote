package vn.ac.vju.mad.converter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class MainActivity : ComponentActivity() {

    companion object {
        private const val DEFAULT_RATE = 167
    }

    private var exchangeRate by mutableIntStateOf(DEFAULT_RATE)
    private var showDialog by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var inputText by mutableStateOf("")

        setContent {
            if (showDialog) {
                AlertDialog(
                    title = {
                        Text(text = "Network error")
                    },
                    text = {
                        Text("Exchange rate is set to the default value")
                    },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("OK")
                        }
                    },
                    onDismissRequest = { },
                    dismissButton = null
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(32.dp)) {

                Text("Convert JPY to VND", fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp))

                CurrencyCard(drawableId = R.drawable.vietnam,
                    contentDescription = "The Vietnam flag",
                    unitString = "VND",
                    unitColor = Color.Red) {

                    Text(
                        convertJpyToVnd(inputText, exchangeRate),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                CurrencyCard(
                    drawableId = R.drawable.japan,
                    contentDescription = "The Japan flag",
                    unitString = "JPY",
                    unitColor = Color.Blue,
                ) {
                    TextField(
                        inputText,
                        onValueChange = { inputText = it },
                        textStyle = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier.weight(1f).padding(8.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        )
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val url = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/vnd.json"
        val onSuccess: (String) -> Unit = {
            Json.parseToJsonElement(it)
                .jsonObject["vnd"]!!
                .jsonObject["jpy"]!!
                .jsonPrimitive.floatOrNull?.apply {
                    exchangeRate = (1f / this).toInt()
                }
        }

        val onError = { _: VolleyError ->
            showDialog = true
            exchangeRate = DEFAULT_RATE
        }
        val req = StringRequest(Request.Method.GET, url, onSuccess, onError)
        val queue = Volley.newRequestQueue(this)
        req.setShouldCache(false)
        queue.add(req)
    }
}

fun convertJpyToVnd(text: String, exchangeRate: Int) =
    text.toIntOrNull()?.let { it * exchangeRate }?.toString() ?: ""

@Composable
fun CurrencyCard(@DrawableRes drawableId: Int,
                 contentDescription: String,
                 unitString: String,
                 unitColor: Color,
                 composable: @Composable () -> Unit) {

    Card(shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth().padding(8.dp)) {

        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(12.dp)) {

            Image(
                painterResource(drawableId),
                contentDescription = contentDescription,
                modifier = Modifier.requiredSize(40.dp)
            )

            composable()

            Text(
                text = unitString,
                color = unitColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
