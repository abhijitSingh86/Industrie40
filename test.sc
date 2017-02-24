import scala.collection.immutable.HashMap
import scala.collection.mutable

val a:List[Int]=List(1,2,4)
val b:List[Int] =List(4,2,1 )

(for(a <- 0 until a.size)yield(b(a))).toList

b.contains(a)
a==b

val map = HashMap("a"->2,"d"->5,"b"->1)


val list = mutable.ArrayBuffer(1,2,3)

list.toSet.filter(_ == 2).head

list -= 5

map.toSeq.sortWith(_._2 < _._2)

var num:Int =2
num += 1
num

val q = mutable.Queue[Int]()
q += 1
q+=2
q.dequeueAll(_ => true)
q


