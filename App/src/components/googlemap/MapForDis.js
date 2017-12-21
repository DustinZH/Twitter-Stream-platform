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



class MapForDis extends Component {
    constructor(props) {
        super();
        this.state = {
            // mapdata: [{lat: 40.7128, lng: -74.0060},
            //     {lat: 43.7128, lng: -75.0060}]
            warning: null
        }
    }
    getdata(){
        let __this = this;
        console.log("eee");
        axios.get('http://127.0.0.1:3004/warning', {

        })
            .then(function (response) {
                console.log(response.data);
                if(response.data === "no data") {
                } else {
                    console.log(response.data);
                    __this.setState({
                        warning: response.data
                    })
                }
            })
            .catch(function (error) {
                console.log(error);
            });
    }
    autoupdate(){
        let __this =  this;console.log("eeee");
        setInterval(__this.getdata(),1000);
    }
    componentWillMount(){
        this.setState({
            image:{
                fire : ['https://image.ibb.co/n6JWzm/if_Fire_116853_1.png', 'https://image.ibb.co/bGABzm/if_Fire_116853_2.png','https://image.ibb.co/nAPGX6/if_Fire_116853_3.png'],
                storm: ['https://image.ibb.co/cnpbX6/if_storm_2_black_110811.png', 'https://image.ibb.co/m8rekR/if_storm_2_black_110811_1.png','https://image.ibb.co/kxmkQR/if_storm_2_black_110811_2.png'],
                gun:['https://image.ibb.co/jwxj26/if_military_gun_pistol_weapon_2537371.png', 'https://image.ibb.co/gCS1h6/if_military_gun_pistol_weapon_2537371_1.png','https://image.ibb.co/mBk6X6/if_refugee1_terrorize_terrorism_violence_person_people_gun_2624392_2.png'],
                hurricane:['https://image.ibb.co/eJLZKm/if_weather_29_1530366_1.png', 'https://image.ibb.co/cRWPKm/if_weather_29_1530366_2.png','https://image.ibb.co/cRWPKm/if_weather_29_1530366_2.png']

            }
        })
        let __this = this;
        // setInterval(
        //     axios.get('http://127.0.0.1:3004/warning', {
        // })
        //     .then(function (response) {
        //         console.log(response.data,'123123');
        //         if(response.data === "no data") {
        //         } else {
        //             console.log(response.data);
        //             __this.setState({
        //                 warning: response.data
        //             })
        //         }
        //     })
        //     .catch(function (error) {
        //         console.log(error);
        //     }),1000)
        axios.get('http://127.0.0.1:3004/warning', {

        })
            .then(function (response) {
                console.log(response.data);
                if(response.data === "no data") {
                } else {
                    console.log(response.data);
                    __this.setState({
                        warning: response.data
                    })
                }
            })
            .catch(function (error) {
                console.log(error);
            });
            __this.autoupdate();
    }
    render() {
        let __this = this;
        console.log(this.state.warning,"mapdatafor dis");
        let MarkerItem = <Marker/>;
        if (this.state.warning !== null) {
             MarkerItem = this.state.warning.map((datas,index) =>
                {
                    let count = datas.count;
                    let pos = {lat : datas.lat, lng: datas.lon};
                    let type = datas.warningtype;
                    console.log(type,index,'kye');
                    if(type === 'storm') {
                        if (count === 1) {
                            return             <Marker
                                key = {index}
                                position = {pos}
                                icon={{
                                    url: __this.state.image.storm[0]
                                }}
                            />
                        }
                        if (count > 1 && count <= 3) {
                            return             <Marker
                                key = {index}
                                position = {pos}
                                icon={{
                                    url: __this.state.image.storm[1]
                                }}
                            />
                        }
                        else {
                            return             <Marker
                                key = {index}
                                position = {pos}
                                icon={{
                                    url: __this.state.image.storm[3]
                                }}
                            />
                        }

                    }
                    else if (type === 'fire'){
                        if (count === 1) {
                            return    <Marker
                                key = {index}
                                position = {pos}
                                icon={{
                                    url: __this.state.image.fire[0]
                                }}
                            />
                        }
                        else if (count > 1 && count <= 3) {
                            return    <Marker
                                key = {index}
                                position = {pos}
                                icon={{
                                    url: __this.state.image.fire[1]
                                }}
                            />
                        }
                        else {
                            return    <Marker
                                key = {index}
                                position = {pos}
                                icon={{
                                    url: __this.state.image.fire[2]
                                }}
                            />
                        }

                    }
                    else if (type === 'hurricane') {
                        if (count === 1) {
                            return    <Marker
                                key = {index}
                                position = {pos}
                                icon={{
                                    url: __this.state.image.hurricane[0]
                                }}
                            />
                        }
                        else if (count > 1 && count <= 3) {
                            return    <Marker
                                key = {index}
                                position = {pos}
                                icon={{
                                    url: __this.state.image.hurricane[1]
                                }}
                            />
                        }
                        else {
                            return    <Marker
                                key = {index}
                                position = {pos}
                                icon={{
                                    url: __this.state.image.hurricane[2]
                                }}
                            />
                        }
                    }
                    else  {
                        if (count === 1) {
                            return    <Marker
                                key = {index}
                                position = {pos}
                                icon={{
                                    url: __this.state.image.gun[0]
                                }}
                            />
                        }
                        else if (count >1 && count <= 3 ) {
                            return    <Marker
                                key = {index}
                                position = {pos}
                                icon={{
                                    url: __this.state.image.gun[1]
                                }}
                            />
                        }
                        else {
                            return    <Marker
                                key = {index}
                                position = {pos}
                                icon={{
                                    url: __this.state.image.gun[1]
                                }}
                            />
                        }
                    }


                }

            );
        }


        let MapWithAMarker = withScriptjs(withGoogleMap(props =>
            <GoogleMap
                defaultZoom={6}
                defaultCenter={{ lat: 40.728199, lng: -73.9894738 }}
            >

                {MarkerItem}

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
export default MapForDis;


