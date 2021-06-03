import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.pow
import kotlin.math.sqrt

sealed class SceneEntity {
    abstract fun update(scene: Scene)
    abstract fun reset()
}

fun randomX(canvasWidth: Float) = (0..canvasWidth.toInt()).random().toFloat()
fun randomColor() = listOf<Color>(
    Color(167, 215, 7),
    Color(254, 191, 64),
    Color(109, 89, 149),
    Color(214, 68, 102),
    Color(255, 137, 59)
).random()

data class Rocket(
    var id: Int,
    var coordinates: Triple<Float, Float, Float> = Triple(0f, 0f, 0f),
    var velocity: Triple<Float, Float, Float> = Triple(0f, -1 * (8..15).random().toFloat(), 0f),
    var acceleartion: Triple<Float, Float, Float> = Triple(0f, 0f, 0f),
    var color: Color = randomColor()
) : SceneEntity() {
    var canvasHeight: Float = 0f
    var canvasWidth: Float = 0f

    fun applyForce(force: Triple<Float, Float, Float>) {
        acceleartion += force
    }

    override fun update(scene: Scene) {
        velocity += acceleartion
        coordinates += velocity
        acceleartion *= 0.0f
        if (coordinates.second >= canvasHeight) {
            reset()
        }
    }

    fun explode(scene: Scene) {
        scene.particles.forEach { (id, rocketParticles) ->
            if (this.id == id) {
                rocketParticles.forEach { particle ->
                    particle.isExplosionReset = true
                    particle.color = color
                    particle.coordinates = coordinates.copy(
                        first = coordinates.first + (-100..100).random(),
                        second = coordinates.second + (-100..100).random(),
                    )
                }
            }
        }
        reset()
    }

    override fun reset() {
        coordinates = Triple(randomX(canvasWidth), canvasHeight, 0f)
        velocity = Triple(0f, -1 * (8..15).random().toFloat(), 0f)
        acceleartion = Triple(0f, 0f, 0f)
        color = randomColor()
    }

}


fun DrawScope.drawRocket(rocket: Rocket) {
    val canvasWidth = size.width
    val canvasHeight = size.height
    rocket.canvasWidth = canvasWidth
    rocket.canvasHeight = canvasHeight
    val (x, y, _) = rocket.coordinates
    drawCircle(
        color = rocket.color,
        radius = (0..24).random().toFloat(),
        center = Offset(x, y)
    )
}


data class Particle(
    var coordinates: Triple<Float, Float, Float> = Triple(0f, 0f, 0f),
    var velocity: Triple<Float, Float, Float> = Triple(0f, -8f, 0f),
    var acceleartion: Triple<Float, Float, Float> = Triple(0f, 0f, 0f),
    var color: Color = randomColor()
) : SceneEntity() {

    var alpha = 1f
    var isExplosionReset = false
    var canvasHeight: Float = 0f
    var canvasWidth: Float = 0f

    fun applyForce(force: Triple<Float, Float, Float>) {
        acceleartion += force
    }

    override fun update(scene: Scene) {
        velocity += acceleartion.copy(
            first = (-2..2).random().toFloat(),
            second = (-2..2).random().toFloat()
        )
        if (alpha < 0) {
            alpha -= 0.01f
        }
        coordinates += velocity
        acceleartion *= 0.0f
        if (isExplosionReset) {
            reset()
        }
    }

    override fun reset() {
        velocity = Triple(0f, 0f, 0f)
        acceleartion = Triple(0f, 0f, 0f)
        alpha = 1f
        isExplosionReset = false
    }

}


fun DrawScope.drawParticles(particle: Particle) {
    val canvasWidth = size.width
    val canvasHeight = size.height
    particle.canvasWidth = canvasWidth
    particle.canvasHeight = canvasHeight
    val (x, y, _) = particle.coordinates
    drawCircle(
        color = particle.color,
        radius = (3..5).random().toFloat(),
        center = Offset(x, y),
        alpha = particle.alpha
    )
}


fun DrawScope.drawPlayer(mouseCoordinates: Pair<Float, Float>, rockets: List<Rocket>, onHit: (Rocket) -> Unit) {
    val canvasWidth = size.width
    val canvasHeight = size.height
    val (x, y) = mouseCoordinates
    val rocket = rockets.firstOrNull() {
        val (rocketX, rocketY, _) = it.coordinates
        val distance = sqrt((y - rocketY).toDouble().pow(2) + (x - rocketX).toDouble().pow(2))
        println(distance < 50.0)
        distance < 50.0
    }
    println(rocket.toString())
    if (rocket != null) {
        onHit(rocket)
    }
    drawCircle(color = Color.White, radius = 10f, center = Offset(x, y))
}






