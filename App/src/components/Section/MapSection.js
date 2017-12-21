import React, { Component} from 'react'
import MapWithAMarker from '../../components/googlemap/googlemap';
import axios from 'axios';
const { MarkerClusterer } = require("react-google-maps/lib/components/addons/MarkerClusterer");


class MapSection extends Component {
    constructor(props) {
        super();
        this.state = {
            tweetdata: []
        }
    }
    componentDidMount() {
        let __this = this;
        axios.get('http://127.0.0.1:3004/tweetCount', {

        })
            .then(function (response) {
                console.log(response.data);
                if(response.data === "no data") {

                } else {
                    console.log(response.data);
                    __this.setState({
                        tweetdata: response.data
                    })
                }
            })
            .catch(function (error) {
                console.log(error);
            });
    }

    render() {
        console.log(this.state.tweetdata,'tweet');
        const orient = this.props.orient || 'orient-right';
        const containerClass ='spotlight style1 ' + orient  + ' content-align-left image-position-center onscroll-image-fade-in';
        return (

            <section className= {containerClass}>
                <div className="content">
                    <h2>Twitter Google Map API</h2>
                    <p>In this Section, You can see the difference in each area using Twitter</p>
                    <ul className="actions vertical">
                        <li><a  className="button">Too see our row data</a></li>
                    </ul>
                </div>
                <div className="image map">
                    <MapWithAMarker
                        googleMapURL="https://maps.googleapis.com/maps/api/js?v=3.exp&libraries=geometry,drawing,places"
                        loadingElement={<div style={{ height: `100%` }} />}
                        containerElement={<div style={{ height: `400px` }} />}
                        mapElement={<div style={{ height: `100%` }} />}
                    />
                </div>
            </section>
        );
    }
}
export default MapSection