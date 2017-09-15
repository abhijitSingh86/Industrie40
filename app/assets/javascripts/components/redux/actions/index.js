`use strict`
var axios = require('axios');
export const GET_SIMULATIONS = 'GET_SIMULATIONS';
export const GET_SIMULATION_WITH_ID = 'GET_SIMULATION_WITH_ID';
export const CHANGE_MAIN_MODE='CHANGE_MAIN_MODE';
export const START_SIMULATION = 'START_SIMULATION';
export const STOP_SIMULATION = 'STOP_SIMULATION';
export const CHANGE_COMPLETION_COUNT='CHANGE_COMPLETION_COUNT';
export const GET_ASSEMBLY_RUNNING_STATUS = 'GET_ASSEMBLY_RUNNING_STATUS'

export const GET_COMPONENT_RUNNING_STATUS = 'GET_COMPONENT_RUNNING_STATUS';
export const GET_SIMULATION_RUNNING_STATUS = 'GET_SIMULATION_RUNNING_STATUS';


export function getSimulationRunningStatus(simulationId){
    return function(dispatch){
        axios.post('/simulation/' + simulationId+ '/runningstatus').then(function (response) {

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
    }
}

export function getComponentRunningStatus(componentId,simulationId){
    return function(dispatch){
        axios.get('/componentStatus/' + simulationId+ '/' + componentId).then(function (response) {

            var action ={
                type:GET_COMPONENT_RUNNING_STATUS,
                payload:response.data
            }
            dispatch(action);
        })

            // .catch(function (error) {
            // _this.setState({
            //     error: "Error while Starting Simulation. \n " + error
            // });
        // })
    }
}

export function getAssemblyRunningStatus(assemblyId,simulationId){
    return function(dispatch){
        axios.get('/assemblyStatus/'+simulationId+'/'+assemblyId).then(function(response){
            console.log(response.data);
            var action = {
                type:GET_ASSEMBLY_RUNNING_STATUS,
                payload:response.data
            };

            dispatch(action);
        });
    };
    // return ({
    //     type: GET_ASSEMBLY_RUNNING_STATUS,
    //     payload: obj
    // });
}

export function updateComponentCompletionCount(obj){
    return {
        type:CHANGE_COMPLETION_COUNT,
        payload:obj
    }
}



function parseResponse(res){
    if(res.responseType === "successEmpty"){
        return true
    }else{
        return res.body
    }

}
export function startSimulation(id){

    return function(dispatch) {
        axios.post('/start/' + id).then(function (response) {
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

export function stopSimulation(id){

    return function(dispatch) {
        axios.post('/stop/' + id).then(function (response) {
            var action = {
                type: STOP_SIMULATION,
                payload: {
                    simulationId: id,
                    response: parseResponse(response.data)
                }
            };
            dispatch(action);
        }).catch(function (error) {
            var action = {
                type: STOP_SIMULATION,
                payload: {
                    simulationId: id,
                    response: error
                }
            };
            dispatch(action);
        })


    }
}

export function changeMainMode(id,mode)  {

   return function(dispatch) {
       axios.get("/simulation/"+id+"/"+mode).then(function(res){
           var action = {
               type: CHANGE_MAIN_MODE,
               payload: {
                   simulationId: id,
                   monitor: true,
                   simulationObj: res.data.body
               }
           };
           dispatch(action);
       }).catch(function(e){
           console.log("error in ChangeMain Mode async call"+e.response.data);
           var action = {
               type: CHANGE_MAIN_MODE,
               payload: {
                   simulationId: id,
                   monitor: false,
                   error:e.response.data
               }
           };
           dispatch(action);
       });
   }

}

export function getAllSimulation(){
    return {
        type:GET_SIMULATIONS
    };
}