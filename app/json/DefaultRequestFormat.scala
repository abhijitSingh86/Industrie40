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

  def getValidationErrorResponse(errorTuples:List[(String,String)]) ={
    Json.obj("responseCode"-> 403 ,"responseType" -> "validationType" , "body" -> errorTuples.map(x=> Json.obj(x._1->x._2)))
  }



}
