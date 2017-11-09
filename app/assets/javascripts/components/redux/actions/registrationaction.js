
var axios = require('axios')

export const INCREMENT_STEP = "INCREMENT_STEP";
export const DECREMENT_STEP = "DECREMENT_STEP";
export const SAVE_FIELD_VALUE_DATA="SAVE_FIELD_VALUE_DATA";
export const SAVE_SIMULATION_DATA="SAVE_SIMULATION_DATA";
export const SAVE_OPERATION_DATA="SAVE_OPERATION_DATA";
export const SAVE_COMPONENT_DATA = "SAVE_COMPONENT_DATA";
export const SAVE_ASSEMBLY_DATA = "SAVE_ASSEMBLY_DATA";
export const SAVE_TRANSPORT_DATA = "SAVE_TRANSPORT_DATA";
export const SUBMIT_DATA_TO_SERVER = "SUBMIT_DATA_TO_SERVER";
export const SUBMIT_DATA_TO_SERVER_FAIL = "SUBMIT_DATA_TO_SERVER_FAIL";

export function resetRegistrationPageState(){
    return ((dispatch) => {dispatch({type:"resetregister",payload:{}})})
}
export function saveFieldValueData(data){
    return function (dispatch) {

        var action ={
            type:SAVE_FIELD_VALUE_DATA,
                payload:data
        }

        dispatch(action);
    }
}

export function submitDataToServer(data){
    return function(dispatch){
        axios.post('/simulation' , data).then( function(response){
            //TODO CHECK THE SERVER RESPONSE
            var action = {
                type:SUBMIT_DATA_TO_SERVER
                ,payload:response.data
            };
            dispatch(action);
        }).catch(function(error){
            var action = {
                type:SUBMIT_DATA_TO_SERVER_FAIL
                ,payload:error.message
            };
            dispatch(action);
        });

    }
}

export function saveTransportFormData(data){
    return function(dispatch){
        var action   ={
            type:SAVE_TRANSPORT_DATA,
            payload : data
        };
        dispatch(action);
    }
}

export function saveAssemblyFormData(data){
    return function(dispatch){
        var action   ={
            type:SAVE_ASSEMBLY_DATA,
            payload : data
        };
        dispatch(action);
    }
}

export function saveComponentFormData(data){
    return function(dispatch){
        var action   ={
         type:SAVE_COMPONENT_DATA,
         payload : data
        };
        dispatch(action);
    }
}

export function saveOperationFormData(data){
    return function(dispatch){
        var action = {
            type:SAVE_OPERATION_DATA,
            payload:data
        };
        dispatch(action);
    };
}

export function saveSimulationFormData(data){
    return function(dispatch) {
        var action = {
            type: SAVE_SIMULATION_DATA,
            payload: data
        };

        dispatch(action);
    }

}

export function incrementStep(){
    return function(dispatch) {
        var action = {
            type: INCREMENT_STEP,
            payload: 1
        };
        dispatch(action);
    }
}

export function decrementStep(){
    return function(dispatch) {
        var action = {
            type: DECREMENT_STEP,
            payload: -1
        };
        dispatch(action);
    }
}