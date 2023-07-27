package android.example.homescout.utils


class RingBuffer<T>(val maxSize: Int) {

    var tail: Int = 0
        private set
    var head: Int = -1
        private set
    private val buffer: MutableList<T> = mutableListOf<T>()
    private var isBufferFull: Boolean = false


    fun head() : Int {
        return head
    }

    fun tail() : Int {
        return tail
    }

    fun put(element: T) {

        if (!isBufferFull) {
            if (head == maxSize - 1) {
                isBufferFull = true
                moveHeadToNextPosition()
                moveTailToNextPosition()
                buffer[head] = element
                return
            }
            head++
            buffer.add(head, element)
            return
        }

        moveHeadToNextPosition()
        moveTailToNextPosition()
        buffer[head] = element
    }

    fun getElementsOrderedTailToHead(): MutableList<T> {

        if (head == -1) {
            return buffer
        }

        val orderedBuffer: MutableList<T> = mutableListOf<T>()
        val currentBufferSize : Int = if (!isBufferFull) head + 1 else maxSize

        for (i in 0 until currentBufferSize) {
            val index = (i + tail) % currentBufferSize
            orderedBuffer.add(i, buffer[index])
        }

        return orderedBuffer
    }

    fun clear() {
        buffer.clear()
        head = 0
        isBufferFull = false
    }


    // PRIVATE FUNCTIONS
    private fun moveTailToNextPosition() {
        tail++
        tail %= maxSize
    }

    private fun moveHeadToNextPosition() {
        head++
        head %= maxSize
    }

    operator fun get(index: Int): T {
        return buffer[index]
    }
}