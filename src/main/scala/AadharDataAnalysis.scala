import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.rdd._

object AadharDataAnalysis{
  def main(args: Array[String]): Unit={

    System.setProperty("hadoop.home.dir","C:/Users/Nitesh/IdeaProjects/")

    var conf=new SparkConf().setAppName("AadharDataAnalysis").setMaster("local")
    var sc=new SparkContext(conf)
    sc.setLogLevel("ERROR")

    var input=sc.textFile("./Input/UIDAI-ENR-DETAIL-20170308.csv")

//    var input=sc.textFile("C:/Users/Nitesh/Desktop/POC's/Data/UIDAI-ENR-DETAIL-20170308.csv")

    var inputwoHeader = input.mapPartitionsWithIndex((i,Itr) => if(i==0) Itr.drop(1) else Itr)

    var mappedInput=inputwoHeader.map(rec=>{
      var temp = rec.split(",")
      (temp(1),temp(2),temp(3),temp(6),temp(8).toInt)
    })

    println("Count Per State" + "\n")
    var countPerState=countPerVariable(mappedInput.map(x=>(x._2,x._5))).take(10).foreach(println)

    println("\n"+ "Count Per Enrollment Agency" + "\n")
    var countPerAgency=countPerVariable(mappedInput.map(x=>(x._1,x._5))).take(10).foreach(println)

    println("\n" + "Top 10 districts with maximum identities generated for both Male")
    var top10DistrictsForMale=countPerVariable(mappedInput.filter(_._4=="M").map(x=>(x._3,x._5))).sortBy(-_._2).take(10).foreach(println)

    println("\n" + "Top 10 districts with maximum identities generated for both Female")
    var top10DistrictsForFemale=countPerVariable(mappedInput.filter(_._4=="F").map(x=>(x._3,x._5))).sortBy(-_._2).take(10).foreach(println)

  }

  def countPerVariable(requiredInput: RDD[(String,Int)]): RDD[(String,Int)] =
  {
    requiredInput.reduceByKey((a,b)=>a+b).sortByKey()
  }
}