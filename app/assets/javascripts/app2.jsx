import React from 'react';
import ReactDom from 'react-dom';
import SimulationMonitor from './components/monitoring/SimulationMonitor';


import '../stylesheets/style.scss';
import '../stylesheets/app.scss';

ReactDom.render((<SimulationMonitor/>),document.getElementById("app2"));