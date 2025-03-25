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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class MainActivity : ComponentActivity() {

    val exchangeRate = mutableStateOf( 171 )

    val showDialog = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inputText = mutableStateOf("")
        setContent {
            if (showDialog.value){
                AlertDialog(
                    title = {
                        Text(text = "Network error")
                    },
                    text = {
                        Text("Exchange rate is set to default value")
                    },
                    confirmButton = {
                        TextButton(
                            onClick = { showDialog.value = false }
                        ) {
                            Text("OK")
                        }
                    },
                    onDismissRequest = { },
                    dismissButton = null
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize().padding(32.dp)
            ) {
                Text(
                    "Convert JPY to VND",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                CurrencyCard(
                    drawableId = R.drawable.vietnam,
                    contentDescription = "Vietnam Flag",
                    inputText = inputText,
                    exchangeRate = exchangeRate,
                    unitString = "VND",
                    unitColor = Color.Red
                )
                CurrencyCard(
                    drawableId = R.drawable.japan,
                    contentDescription = "Japan Flag",
                    inputText = inputText,
                    exchangeRate = exchangeRate,
                    unitString = "JPY",
                    unitColor = Color.Blue
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val url = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/vnd.json"
        val onSuccess = { jsonString: String ->
            val data = Json.parseToJsonElement(jsonString)
            val vndData = data.jsonObject["vnd"]
            if (vndData != null) {
                val jpyData = vndData.jsonObject["jpy"]
                if (jpyData != null) {
                    val rate = jpyData.jsonPrimitive.floatOrNull
                    if (rate != null) {
                        exchangeRate.value = (1f / rate).toInt()
                    }
                }
            }
        }
    }
    val onError = { error: VolleyError ->
        showDialog.value = true
        exchangeRate.value = 167
    }
    val req = StringRequest(Request.Method.GET, url, onSuccess, onError)
    val queue = Volley.newRequestQueue(this)
    req.setShouldCache(false)
    queue.add(req)
}

@Composable
fun CurrencyCard(
    @DrawableRes drawableId: Int,
    contentDescription: String,
    inputText: MutableState<String>,
    exchangeRate: MutableState<Int>,
    unitString: String,
    unitColor: Color
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) {
            Image(
                painter = painterResource(drawableId),
                contentDescription = contentDescription,
                modifier = Modifier.requiredSize(40.dp)
            )
            if (unitString == "VND") {
                Text(
                    text = convertJpyToVnd(inputText.value, exchangeRate.value),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                TextField(
                    value = inputText.value,
                    onValueChange = { inputText.value = it },
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.weight(1f).padding(8.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )
            }
            Text(
                text = unitString,
                color = unitColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun convertJpyToVnd(text: String, exchangeRate: Int): String {
    val i = text.toIntOrNull()
    return if (i == null) {
        ""
    } else {
        (i * 171).toString()
    }
}
