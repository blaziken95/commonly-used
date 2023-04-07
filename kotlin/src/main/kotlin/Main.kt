package com.app.testApp

import com.opencsv.CSVReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.io.FileReader
import java.math.BigInteger
import java.nio.charset.StandardCharsets

@SpringBootApplication
class Main

suspend fun main(args: Array<String>) {
    /** coroutine test **/
    fun exampleSuspend() {
        val job3 = CoroutineScope(Dispatchers.IO).async {
            //  #2 IO Thread 에서 작업3를 수행한다
            (1..10000).sortedByDescending { it }
            //  #5 작업3이 완료된다
        }

        val job1 = CoroutineScope(Dispatchers.Main).launch {
            //  #1 Main Thread 에서 작업1을 수행한다
            println(1)

            //  #3 작업1의 남은 작업을 위해 작업3으로부터 결과값이 필요하기 때문에 Main Thread는 작업1을 일시중단한다
            val job3Result = job3.await()
            //  #6 작업3으로부터 결과를 전달받는다
            //  #7 작업1이 재개된다
            job3Result.forEach {
                println(it)
            }

            //  #4 Main Thread 에서 작업2가 수행되고 완료된다
            val job2 = CoroutineScope(Dispatchers.Main).launch {
                println("Job2 수행완료")
            }
        }
    }

    /** csv parsing */
    fun readCsv(fileName: String) {
        val fr = FileReader(fileName, StandardCharsets.UTF_8)

        var insertString = "insert into mission_nft (token_id, card_name, card_type, thumbnail_image, max_limit, token_uri, card_id, is_active, description) values "

        fr.use {
            val reader = CSVReader(fr)

            reader.use { r ->
                var line = r.readNext()
                if (line[0] == "Token ID") line = r.readNext()
                while (line != null) {
                    insertString += Row(
                        tokenId = line[0].toBigInteger(),
                        cardId = line[1].toString(),
                        cardName = line[2].toString(),
                        description = line[3].toString(),
                        thumbnailImage = line[4].toString(),
                        cardType = line[5].toString(),
                        maxLimit = line[6].toLong(),
                        tokenUri = line[7].toString()
                    )
                    line = r.readNext()
                }
            }
        }
        println(insertString)
    }
//    readCsv("test.csv");

    /** query builder **/
    fun buildQuery() {
        val actionEventId1 = 14L
        val actionEventId2 = 15L
        var insertQuery = "insert into action_event_prerequisites (action_event_id, reward_id_start_range, reward_id_end_range, token_id_start_range, token_id_end_range, amount, reward_id) values "
        var rangeString = ""
        for (i in 50001..52000) {
            if (i % 50000 % 3 == 0) {
                if (rangeString.length > 1) {
                    insertQuery += "($actionEventId2, 3, 3, ${rangeString.trim(',')}, 1, 4),"
                }
                insertQuery += "($actionEventId1, 3, 3, $i, $i, 1, 4),"
                rangeString = ""
            } else {
                rangeString += "$i,"
            }
        }
        if (rangeString.length > 1) {
            insertQuery += "($actionEventId2, 3, 3, ${rangeString.trim(',')}, 1, 4),"
        }
        insertQuery = insertQuery.trim(',')
        insertQuery +=
            ",($actionEventId1, 3, 3, 60001, 60001, 1), ($actionEventId1, 3, 3, 60002, 60002, 1), ($actionEventId1, 3, 3, 60003, 60003, 1), ($actionEventId1, 3, 3, 60004, 60004, 1)," +
                    "($actionEventId2, 3, 3, 60001, 60001, 1), ($actionEventId2, 3, 3, 60002, 60002, 1), ($actionEventId2, 3, 3, 60003, 60003, 1), ($actionEventId2, 3, 3, 60004, 60004, 1)"
        println(insertQuery)
    }
//    buildQuery()
}

data class Row (
    val tokenId : BigInteger,
    val cardName : String,
    val cardType : String,
    val thumbnailImage : String,
    val maxLimit : Long = 1,
    val tokenUri : String,
    val cardId : String,
    val isActive : Boolean = false,
    val description : String
) {
    override fun toString() : String {
        return "($tokenId,'$cardName','$cardType','$thumbnailImage',$maxLimit,'$tokenUri','$cardId',$isActive,'$description'),"
    }
}