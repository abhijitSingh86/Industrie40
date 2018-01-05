import {SAVE_SIMULATION_DATA , INCREMENT_STEP , DECREMENT_STEP
    ,SAVE_OPERATION_DATA
,SAVE_COMPONENT_DATA
,SAVE_ASSEMBLY_DATA
,SAVE_TRANSPORT_DATA
,SUBMIT_DATA_TO_SERVER
,SUBMIT_DATA_TO_SERVER_FAIL
,SAVE_FIELD_VALUE_DATA

} from "../actions/registrationaction"

import {RESET_REGISTRATION_DETAILS} from '../actions/index';


const initialState = {
    step:0,
    renderSuccess:false,
    responseMessage:"",
    isError:false,
    simulationId:-1,
    simulationVersionId:-1,
    fieldValues:{
        simulationName     : "",
        simulationDesc    : "",
        operations : [],//{id:0,label:"0"},{id:1,label:"1"},{id:2,label:"2"},{id:3,label:"3"}],
        components      : [],
        assemblies   : [],
        operationCounter:0,
        componentCounter:0,
        assemblyCounter:0,
        assemblyTT:[],
        componentTT:[]
    }
}

export default function registration(state =initialState ,action){

    switch(action.type){


        case RESET_REGISTRATION_DETAILS: {
            var resetPay = {
                simulationName     : "",
                simulationDesc    : "",
                operations : [],//{id:0,label:"0"},{id:1,label:"1"},{id:2,label:"2"},{id:3,label:"3"}],
                components      : [],
                assemblies   : [],
                operationCounter:0,
                componentCounter:0,
                assemblyCounter:0,
                assemblyTT:[],
                componentTT:[]
            }
            return {...state , fieldValues : resetPay , step:0 ,renderSuccess:false,
                responseMessage:"",
                isError:false,
                simulationId:-1,
                simulationVersionId:-1}
        }
        case SAVE_FIELD_VALUE_DATA:{
            var state = Object.assign({},state,{
                fieldValues : action.payload
            });
            return state;
        }
        case SUBMIT_DATA_TO_SERVER:{
            var state = Object.assign({},state,{
                responseMessage :"success",
                isError:false,
                renderSuccess:true,
                simulationId:action.payload.body.s.id,
                simulationVersionId:action.payload.body.s.versionId,
                fieldValues:initialState.fieldValues
            });

            return state;
        }
        case SUBMIT_DATA_TO_SERVER_FAIL:{
            var state = Object.assign({},state,{
                responseMessage : action.payload,
                isError:true

            });

            return state;
        }
        case SAVE_ASSEMBLY_DATA:{
            var newFieldValues = Object.assign({},state.fieldValues,{
                assemblies : action.payload.assemblies,
                assemblyCounter : action.payload.assemblyCounter
            });

            var state = Object.assign({},state,{
                fieldValues : newFieldValues
            });

            return state;
        }

        case SAVE_OPERATION_DATA:{
            var newFieldValues = Object.assign({},state.fieldValues,{
                operations : action.payload.operations,
                operationCounter : action.payload.operationCounter
            });

            var state = Object.assign({},state,{
                fieldValues : newFieldValues
            });

            return state;
        }

        case SAVE_COMPONENT_DATA:{
            var newFieldValues = Object.assign({},state.fieldValues,{
                components : action.payload.components,
                componentCounter : action.payload.componentCounter
            });

            var state = Object.assign({},state,{
                fieldValues : newFieldValues
            });

            return state;
        }

        case SAVE_SIMULATION_DATA:{

            var newFieldValues = Object.assign({},state.fieldValues,{
                simulationName : action.payload.simulationName,
                simulationDesc : action.payload.simulationDesc
            });

            var state = Object.assign({},state,{
                fieldValues : newFieldValues
            });

            return state;
        }

        case INCREMENT_STEP :{
            var state = Object.assign({},state,
                {
                    step:state.step+action.payload
                });
            return state;
        }

        case SAVE_TRANSPORT_DATA : {
            var newFieldValues = Object.assign({},state.fieldValues,{
                componentTT : action.payload.componentTT,
                assemblyTT : action.payload.assemblyTT
            });

            return {...state , fieldValues : newFieldValues}
        }

        case DECREMENT_STEP :{
            var state = Object.assign({},state,
                {
                    step:state.step+action.payload
                    ,renderSuccess:false
                    ,isError:false
                });
            return state;
        }

        default:
            return state;
    }

}