import React, { Component} from 'react'

class StartSection extends Component {
    action(){
        window.scrollTo({
            "behavior": "smooth",
            "left": 0,
            "top": 640
        });
    }
    render() {
        return (
            <section className="banner style1 orient-left content-align-left image-position-right fullscreen onload-image-fade-in onload-content-fade-right">
                <div className="content">
                    <h1>Explore Big Data</h1>
                    <p className="major">
                        Exploring Twitter Data using different tools in big data area
                    </p>
                    <ul className="actions vertical">
                        <li><a onClick={this.action} id = 'startClick' className="button big wide smooth-scroll-middle">Get Started</a></li>
                    </ul>
                </div>
                <div className="image">
                    <img src={require('../../images/banner.jpg')} alt="" />
                </div>
            </section>
        );
    }
}
export default StartSection