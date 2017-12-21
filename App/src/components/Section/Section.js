import React, { Component} from 'react';
import option1 from './option1';
import ReactEcharts from 'echarts-for-react';
import axios from 'axios';


class Section extends Component {

    constructor(props) {
        super(props);
        this.state = {
            datax : [],
            positive:[],
            negative:[]
        }
        this.getOption = this.getOption.bind(this);
        this.pushDate = this.pushDate.bind(this);
        this.pos = this.pos.bind(this);
        this.neg = this.neg.bind(this);
    }
    getOption() {
        const option = {
            title: {
                text: 'Sentimental Analysis'
            },
            tooltip: {
                trigger: 'axis'
            },

            grid: {
                left: '3%',
                right: '4%',
                bottom: '3%',
                containLabel: true
            },
            toolbox: {
                feature: {
                    saveAsImage: {}
                }
            },
            xAxis: {
                type: 'category',
                boundaryGap: false,
                data: this.state.datax
            },
            yAxis: {
                type: 'value'
            },
            series: [
                {
                    name:'Positive',
                    type:'line',
                    data:this.state.positive
                },
                {
                    name:'Negative',
                    type:'line',
                    data:this.state.negative
                }
            ]
        };
        return option;
    }
    pushDate() {
        let datax = this.state.datax;
        datax.push(new Date().toLocaleTimeString());
        return datax;
    }
    pos(x) {
        let positive = this.state.positive;
        positive.push(x);
        return positive;
    }
    neg(x) {
        let negative = this.state.negative;
        negative.push(x);
        return negative;
    }
    componentWillMount() {
        let __this = this;
        axios.get('http://127.0.0.1:3004/warning', {

        })
            .then(function (response) {
                console.log(response.data);
                if(response.data === "no data") {
                    console.log("nodata");
                } else {
                    console.log(response.data,'warningdaata');
                }
            })
            .catch(function (error) {
                console.log(error);
            })
        // let __this = this;
        // console.log(__this.state.datax,__this.state.positive);
        // console.log(this);
        // setInterval(()=> {
        //         __this.setState({
        //             datax : __this.pushDate(),
        //             positive:__this.pos(Math.random()*100),
        //             negative:__this.neg(Math.random()*105)
        //         });
        // }, 1000)
        setInterval(()=> {
            axios.get('http://127.0.0.1:3004/tweetCount', {

            })
                .then(function (response) {
                    console.log(response.data);
                    if(response.data === "no data") {
                        console.log("nodata");
                    } else {
                        console.log(response.data,'ajax');
                        __this.setState({
                            datax:__this.pushDate(),
                            positive:__this.pos(response.data[0].positive),
                            negative:__this.neg(response.data[0].negative)
                        });
                    }
                })
                .catch(function (error) {
                    console.log(error);
                })
        }, 1000);

    }

    click() {
        window.open('http://localhost:3004/tweetCount');
    }
    render() {
        const orient = this.props.orient || 'orient-right';
        const containerClass ='spotlight style1 ' + orient  + ' content-align-left image-position-center onscroll-image-fade-in';
        return (

            <section className= {containerClass}>
                <div className="content">
                    <h2>Sentimental Analysis</h2>
                    <p>In this section, we are conducting sentimental analysis to different tweets. </p>
                    <ul className="actions vertical">
                        <li><a onClick={this.click} className="button">Too see our row data</a></li>
                    </ul>
                </div>
                <div className="image">
                    <ReactEcharts
                        option={this.getOption()}/>
                </div>
            </section>
        );
    }
}
export default Section