import me.tongfei.progressbar.ProgressBar
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


private fun deleteDirectory(directoryToBeDeleted: File): Boolean {
    val allContents = directoryToBeDeleted.listFiles()
    if (allContents != null) {
        for (file in allContents) {
            deleteDirectory(file)
        }
    }
    return directoryToBeDeleted.delete()
}

@Throws(IOException::class)
fun copyDirectory(sourceDirectoryLocation: String, destinationDirectoryLocation: String) {
    Files.walk(Paths.get(sourceDirectoryLocation))
        .forEach { source ->
            val destination = Paths.get(
                destinationDirectoryLocation,
                source.toString().substring(sourceDirectoryLocation.length)
            )
            try {
                Files.copy(source, destination)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
}

fun regenerateCache(profileName: String) {
    val source = "D:\\youtube\\profile_hack\\${profileName}"
    val destination = "C:\\Users\\rhein\\AppData\\Local\\Google\\Chrome\\User Data\\${profileName}"
    val path = Paths.get(destination)

    deleteDirectory(path.toFile())

    println("success to delete")

    Thread.sleep(5000)

    copyDirectory(source, destination)

}

fun killBrowser() {
    try {
        val p = Runtime.getRuntime().exec("taskkill /F /IM chrome.exe")
        p.waitFor()
    } catch (e: Exception) { e.printStackTrace() }
}

fun openBrowser() {
    try {
        for (l in list) {
            Runtime.getRuntime().exec("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe https://www.youtube.com --profile-directory=\"${l}\"")
        }
    } catch (e: Exception) { e.printStackTrace() }
}

fun callScheduler() {
    try {
        val pb = ProgressBar(JOB_NAME, MAX_TIME.toLong())
        openBrowser()
        val scheduler = Executors.newScheduledThreadPool(1)
        val runnable: Runnable = object : Runnable {
            var countdownStarter = MAX_TIME
            override fun run() {
                pb.step()
                countdownStarter--
                if (countdownStarter < 0) {
                    println("Timer Over!")
                    scheduler.shutdown()

                    killBrowser()

                    Thread.sleep(5000)

                    for (l in list) {
                        regenerateCache(l)
                    }

                    callScheduler()
                }
            }
        }
        scheduler.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private val list = arrayOf("Profile 4", "Profile 27", "Profile 40", "Profile 6")
private const val MAX_TIME = 4710
private const val JOB_NAME = "Wait for restart"

fun main(args: Array<String>) {
//    C:\Users\rhein\AppData\Local\Google\Chrome\User Data\Profile 9
//    C:\Program Files\Google\Chrome\Application\chrome.exe

    // akun yang akan dimainkan
    // 1. abunohaboka => Profile 4
    // 2. agustinsauer846 => Profile 27
    // 3. amirantungo => Profile 40
    // 4. baharbinatang => Profile 6

    //1. Buat list yang memuat semua akun
    callScheduler()

//    callScheduler()
}
