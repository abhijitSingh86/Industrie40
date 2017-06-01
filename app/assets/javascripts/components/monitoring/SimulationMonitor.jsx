var React = require('react');


class SimulationMonitor extends React.Component{

    render(){

        return (
            <div>
                <p>Simulation Start Page</p>
                <p>Copy paste the content in sh file and execute in component and distribution directory</p>
                <pre></pre>
                <p>Once Completed and all components turn green press on start to Start the simulation </p>
                <form method="POST" action="/simulationStatus">
                <input type="submit" value="start" />
                </form>
            </div>
        );
    }
}

module.exports = SimulationMonitor