import {
    SETUP_FOR_RERUN,
    SETUP_FOR_RERUN_FAIL,
    SETUP_FOR_CLONE,
    SETUP_FOR_CLONE_FAIL
,SIMULATION_VERSION_FETCH
,SIMULATION_VERSION_FETCH_FAIL} from "../actions/startpageaction";


const initialState = {

    responseMsg:"",
    versionsFetched:false
    ,versions:[]

};

export default function startpage(state = initialState , action){

    switch(action.type){
        case "resetVersion":{
            return {...state,responseMsg:"",versions:[],versionsFetched:false}
        }

        case "reset" :{
            return initialState;
        }
        case SIMULATION_VERSION_FETCH:{

            return {...state,responseMsg:"Please select appropriate version first",versions:action.payload,versionsFetched:true}

        }
        case SIMULATION_VERSION_FETCH_FAIL:{
            return {...state,responseMsg:"Error occurred during version info fetch"+action.payload}
        }
        case SETUP_FOR_RERUN: {

            var state = {...state,responseMsg:"Previous Data reset complete.. starting monitoring mode" };

            return state;
        }

        case SETUP_FOR_RERUN_FAIL: {

            var state = {...state,responseMsg:"Error during previous data clear. <br/>"+action.payload }
            return state;
        }

        case SETUP_FOR_CLONE_FAIL:{
            var state = {...state,responseMsg:"Error retrieving previous simulation data.<br/>"+action.payload};
            return state;
        }

        case SETUP_FOR_CLONE:{
            var state = {...state,responseMsg:"Clone data retrieved.. loading registration panel."}
            return state;
        }
        default:
            return state;

    }
}