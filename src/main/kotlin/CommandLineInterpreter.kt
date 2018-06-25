import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object CommandLineInterpreter {


    private fun executeCommandWithArgs(commandInput: String) {
        val rt = Runtime.getRuntime()
        val errorReported: printOutput
        val outputMessage: printOutput

        try {
            rt.exec("quicktype ")

            val proc = rt.exec(commandInput)
            errorReported = getStreamWrapper(proc.errorStream, "ERROR")
            outputMessage = getStreamWrapper(proc.inputStream, "OUTPUT")
            errorReported.start()
            outputMessage.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun executeCommand(commandInput: String, waitFor: Boolean = false) {
        val rt = Runtime.getRuntime()
        val errorReported: printOutput
        val outputMessage: printOutput

        try {
            val proc = rt.exec(commandInput)
            errorReported = getStreamWrapper(proc.errorStream, "ERROR")
            outputMessage = getStreamWrapper(proc.inputStream, "OUTPUT")
            errorReported.start()
            outputMessage.start()

            proc.waitFor()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    fun getStreamWrapper(`is`: InputStream, type: String): printOutput {
        return printOutput(`is`, type)
    }

    class printOutput internal constructor(`is`: InputStream, type: String) : Thread() {
        internal var `is`: InputStream? = null

        init {
            this.`is` = `is`
        }

        override fun run() {
            var s: String? = null
            try {
                val br = BufferedReader(
                        InputStreamReader(`is`!!))

                do {
                    s = br.readLine()
                    if(s != null) println(s)
                } while (s != null)

            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }

        }
    }

}