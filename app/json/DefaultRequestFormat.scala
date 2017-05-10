package json

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
  * Created by billa on 07.01.17.
  */
object DefaultRequestFormat {

  /*
  {
    responseCode:
    responseType:
    body : {
    }
    validationType:{
      [{
        error:
        element:
        }
      ]
    }
    successEmpty:

    success:{
      object
    }

   */

  def getEmptySuccessResponse() ={
    Json.obj("responseCode"-> 200,"responseType" -> "successEmpty" , "body" -> Json.obj())
  }

  def getSuccessResponse(data:JsValue)={
    Json.obj("responseCode"-> 200,"responseType" -> "success" , "body" -> data)
  }

  def getValidationErrorResponse(errorTuples:List[(String,String)]) ={
    Json.obj("responseCode"-> 403 ,"responseType" -> "validationType" , "body" -> errorTuples.map(x=>
      Json.obj("element" ->x._1 , "errormsg"->x._2)))
  }



}
