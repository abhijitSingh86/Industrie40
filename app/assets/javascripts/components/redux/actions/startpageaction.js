var axios = require('axios')

import {SAVE_FIELD_VALUE_DATA} from "./registrationaction"
export const SETUP_FOR_RERUN= "SETUP_FOR_RERUN";
export const SETUP_FOR_RERUN_FAIL= "SETUP_FOR_RERUN_FAIL";
export const SETUP_FOR_CLONE= "SETUP_FOR_CLONE";
export const SETUP_FOR_CLONE_FAIL= "SETUP_FOR_CLONE_FAIL";


export function resetStartPageState(){
    return ((dispatch) => {dispatch({type:"reset",payload:{}})})
}

export function cloneSetup(key){
    return function(dispatch){
        axios.get('/simulation/'+key+'/clone').then(function (response) {

            var action = {
                type: SETUP_FOR_CLONE,
                payload:response.data
            };

            var actionRe ={
                type:SAVE_FIELD_VALUE_DATA,
                payload:action.payload
            };

            var actionRunningMode={
                type:"MODE",
                payload:"registration"
            };

            dispatch(action);
            dispatch(actionRe);
            dispatch(actionRunningMode);

        }).catch(function (error) {
            var action = {
                type: SETUP_FOR_CLONE_FAIL,
                payload:error
            };
            dispatch(action);
        })

    }
}


export function setupforRerun(key){
    return function(dispatch){
        axios.post('/simulation/'+key+'/clear').then(function (response) {

            var action = {
                type:SETUP_FOR_RERUN
                ,payload:response.data
            };
            dispatch(action);


        }).catch(function (error) {
            var action = {
                type:SETUP_FOR_RERUN_FAIL
                ,payload:error.message
            };
            dispatch(action);
        })
    }
}
