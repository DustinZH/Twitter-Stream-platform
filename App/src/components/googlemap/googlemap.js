import React, { Component } from 'react';
import {
    withScriptjs,
    withGoogleMap,
    GoogleMap,
    Marker
} from "react-google-maps";
import data from '../../data/latlng';
import axios from 'axios';
const { MarkerClusterer } = require("react-google-maps/lib/components/addons/MarkerClusterer");
//https://tomchentw.github.io/react-google-maps/#introduction
// AIzaSyBxaQHvg4j8nBlFVFqPmDFlUTs711MZVJo



class MapWithAMarker extends Component {
        constructor(props) {
            super();
            this.state = {
                mapdata: []
            }
        }
        componentDidMount() {
            let __this = this;
            axios.get('http://127.0.0.1:3004/Mapdata', {

            })
                .then(function (response) {
                    console.log(response.data);
                    if(response.data === "no data") {
                    } else {
                        console.log(response.data);
                        __this.setState({
                            mapdata: response.data
                        })
                    }
                })
                .catch(function (error) {
                    console.log(error);
                });
        }
        render() {
            console.log(this.state.mapdata,"mapdata");
            let MarkerItem = this.state.mapdata.map((datas,index) =>
                <Marker
                    key = {index}
                    position = {datas}
                />
            );

            let MapWithAMarker = withScriptjs(withGoogleMap(props =>
                <GoogleMap
                    defaultZoom={6}
                    defaultCenter={{ lat: 40.728199, lng: -73.9894738 }}
                >
                    <MarkerClusterer
                        averageCenter={ true }
                        enableRetinaIcons={ true }
                        gridSize={ 150 }>
                    {MarkerItem}
                    </MarkerClusterer>
                </GoogleMap>
            ));
            console.log(MarkerItem,"Marker");
            return (
                    <div>
                        <MapWithAMarker
                            googleMapURL= {this.props.googleMapURL}
                            loadingElement={this.props.loadingElement}
                            containerElement={this.props.containerElement}
                            mapElement={this.props.mapElement}
                        />
                    </div>
            );
        }
    }
    export default MapWithAMarker;


