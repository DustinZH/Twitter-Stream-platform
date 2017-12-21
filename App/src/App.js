import './App.css';
import React, { Component } from 'react';
import Section from '../src/components/Section/Section';
import MapSection from '../src/components/Section/MapSection';
import StartSection from '../src/components/Section/StartSection';
import WarningMap from '../src/components/Section/WarningMap';
const { MarkerClusterer } = require("react-google-maps/lib/components/addons/MarkerClusterer");


class App extends Component {
  render() {
    return (
      <div className="App">
          <div id="wrapper" className="divided">
              <StartSection/>
              <MapSection/>

              <Section orient="right"/>

              <WarningMap orient="orient-right"/>

              <section className="wrapper style1 align-center">
                  <div className="inner">
                      <h2>Thank you for visiting</h2>
                  </div>
              </section>
          </div>
      </div>
    );
  }
}

export default App;
