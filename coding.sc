import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/*
1-> iterate through the first list
2-> check if it greater then next.. if yes remove it and add it to other list
3-> if other list exceeds by 2 terminate with false else true
 */

def almostIncreasingSequence(sequence: Array[Int]): Boolean = {
  import scala.collection.mutable
  import scala.collection.mutable.ArrayBuffer
  var b=Int.MinValue
  var buf:mutable.ArrayBuffer[Int] = ArrayBuffer()
  for(i <- 0 until sequence.length){
    if(i+1 <= sequence.length-1) {
      if(sequence(i) < sequence(i+1)){
        buf += sequence(i)
      }

    }else{
      buf += sequence(i)
    }
  }

  println(buf.mkString(" : "))
  println(sequence.mkString(" : "))

  val v=buf.foldLeft(Int.MinValue)((a,b)=> if(a<b) {
    b
  }else
      a)
  println(v+":"+buf.indexOf(v)+":"+
    (buf.indexOf(v)!= buf.length-1)+
    ":"+(sequence.size - buf.distinct.size >1))
  if(buf.indexOf(v)!= buf.length-1){
    return false
  }
  if(sequence.size - buf.size >1)
    false
  else
    true
}
val seq = Array(1,2,3,4,3,6)//Array(40, 50, 60, 10, 20, 30)

val v=seq.foldLeft(Int.MinValue)((a,b)=> if(a<b) {
  b
}else
 a)
seq.indexOf(v)!= seq.length-1

almostIncreasingSequence(seq)