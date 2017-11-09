`use strict`
var axios = require('axios');
export * from './registrationaction';
export * from './startpageaction'
export const GET_SIMULATIONS = 'GET_SIMULATIONS';
export const GET_SIMULATION_WITH_ID = 'GET_SIMULATION_WITH_ID';
export const CHANGE_MAIN_MODE='CHANGE_MAIN_MODE';
export const START_SIMULATION = 'START_SIMULATION';
export const STOP_SIMULATION = 'STOP_SIMULATION';
export const CHANGE_COMPLETION_COUNT='CHANGE_COMPLETION_COUNT';
export const GET_SIMULATION_RUNNING_STATUS = 'GET_SIMULATION_RUNNING_STATUS';
export const RESET_REGISTRATION_DETAILS = "RESET_REGISTRATION_DETAILS";
export const SIMULATION_ONLINE_CHECK = 'SIMULATION_ONLINE_CHECK';

export function resetSimulationAndMainMode(){
    return function(dispatch){

        var actionMM = {
            type:"resetMainMode",
            payload:{}
        }

        var actionS = {
        type:"resetSimulation",
        payload:{}
        }

        dispatch(actionMM);
        dispatch(actionS)
    }


}


export function simulationLoadingCheck(){
    return function(dispatch) {
        axios.get('/simulation/onlinecheck').then(function (response) {
            var action = {
                type: SIMULATION_ONLINE_CHECK,
                payload: {
                    isLoadingComplete:response.data.body.isLoadingComplete
                }
            };
            dispatch(action);
        }).catch(function (error) {
            var action = {
                type: SIMULATION_ONLINE_CHECK,
                payload: {
                    response: error
                }
            };
            dispatch(action);
        })
    }
}



export function getSimulationRunningStatus(simulationId){
    return function(dispatch){
        axios.post('/simulation/' + simulationId+'/runningstatus').then(function (response) {

            var action ={
                type:GET_SIMULATION_RUNNING_STATUS,
                payload:response.data
            }
            dispatch(action);
        })

        // .catch(function (error) {
        // _this.setState({
        //     error: "Error while Starting Simulation. \n " + error
        // });
        // })
    };
}

export function recordRunningMode(mode){

    return function(dispatch){
            var action ={
                type:"MODE",
                payload:mode
            }
            dispatch(action);
    }
}

function parseResponse(res){
    if(res.responseType === "successEmpty"){
        return true
    }else{
        return res.body
    }

}
export function startSimulation(id,versionId){

    return function(dispatch) {
        axios.post('/start/' + id +'/'+versionId).then(function (response) {
                var action = {
                    type: START_SIMULATION,
                    payload: {
                        simulationId: id,
                        response: parseResponse(response.data)
                    }
                };
                dispatch(action);
        }).catch(function (error) {
            var action = {
                type: START_SIMULATION,
                payload: {
                    simulationId: id,
                    response: error
                }
            };
            dispatch(action);
        })


    }
}



export function stopSimulation(id , mode){

    return function(dispatch) {
        axios.post('/stop/' + id+'/'+mode).then(function (response) {
            var action = {
                type: STOP_SIMULATION,
                payload: {
                    simulationId: id,
                    response: parseResponse(response.data)
                    ,mode:mode
                }
            };
            dispatch(action);
        }).catch(function (error) {
            var action = {
                type: STOP_SIMULATION,
                payload: {
                    simulationId: id,
                    response: error
                    ,mode:mode
                }
            };
            dispatch(action);
        })


    }
}

export function resetRegistrationForm(){
    return function(dispatch) {
        dispatch({
            type:RESET_REGISTRATION_DETAILS,
            payload:""
        });
    }
}

export function changeMainMode(id,mode)  {

    console.log("in idex.js changeMainMode")
    console.log(mode);
   return function(dispatch) {

       if(mode === "reset"){
           var action = {
               type: CHANGE_MAIN_MODE,
               payload: {
                   simulationId: id,
                   monitor: false,
                   pagemode:mode,
                   error:"Home button Clicked"
               }
           };
           dispatch(action);


       }else {

           axios.get("/simulation/" + id + "/" + mode).then(function (res) {
               var action = {
                   type: CHANGE_MAIN_MODE,
                   payload: {
                       simulationId: id,
                       monitor: true,
                       mode: mode,
                       pagemode:"monitor",
                       simulationObj: res.data.body
                   }
               };
               dispatch(action);
           }).catch(function (e) {
               console.log("error in ChangeMain Mode async call" + e.response + " : " + id + " : " + mode);
               var action = {
                   type: CHANGE_MAIN_MODE,
                   payload: {
                       simulationId: id,
                       monitor: false,
                       mode: mode,
                       pagemode:"reset",
                       error: e.response.data
                   }
               };
               dispatch(action);
           });
       }
   }

}

export function getAllSimulation(){
    return {
        type:GET_SIMULATIONS
    };
}