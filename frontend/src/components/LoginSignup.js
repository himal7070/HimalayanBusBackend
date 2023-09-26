import React, { Component } from 'react';
import '../styles/LoginSignup.css';

class LoginSignup extends Component {
    constructor(props) {
        super(props);
        this.state = {
            activeTab: 'login',
        };
    }

    handleTabChange = (tab) => {
        this.setState({ activeTab: tab });
    };

    render() {
        const { activeTab } = this.state;

        return (

            <div className="login-container">
                <img src="/img/logobus.png" alt="Welcome Image" className="welcome-image" />
                <p style={{ color: 'black' }}>Plan your bus trips with ease! </p>

                <div className="slider">
                    <label
                        htmlFor="loginTab"
                        className={`tab-label ${activeTab === 'login' ? 'active' : ''}`}
                        onClick={() => this.handleTabChange('login')}
                    >
                        Login
                    </label>

                    <label
                        htmlFor="signupTab"
                        className={`tab-label ${activeTab === 'signup' ? 'active' : ''}`}
                        onClick={() => this.handleTabChange('signup')}
                    >
                        Signup
                    </label>

                    <div className={`slider-tab ${activeTab === 'signup' ? 'right' : 'left'}`} />
                </div>
                {activeTab === 'login' && (
                    <form>
                        <div className="form-group">
                            <label htmlFor="loginEmail"></label>
                            <input type="text" className="form-control" id="loginEmail" placeholder="Email Address" required />
                        </div>
                        <div className="form-group">
                            <label htmlFor="loginPassword"></label>
                            <input type="password" className="form-control" id="loginPassword" placeholder="Password" required />
                        </div>
                        <a href="#">Forgot password?</a>
                        <button type="submit" className="btn btn-primary btn-block mt-3">Login</button>
                        <p className="text-center mt-3">Not a member? <a href="#signup-tab" data-toggle="tab">Signup now</a></p>
                    </form>
                )}

                {activeTab === 'signup' && (
                    <form>
                        <div className="form-group">
                            <label htmlFor="fullName"></label>
                            <input type="text" className="form-control" id="fullName" placeholder="Full Name" required />
                        </div>
                        <div className="form-group">
                            <label htmlFor="signupEmail"></label>
                            <input type="email" className="form-control" id="signupEmail" placeholder="Email Address" required />
                        </div>
                        <div className="form-group">
                            <label htmlFor="mobileNumber"></label>
                            <input type="text" className="form-control" id="mobileNumber" placeholder="Mobile Number" required />
                        </div>
                        <div className="form-group">
                            <label htmlFor="signupPassword"></label>
                            <input type="password" className="form-control" id="signupPassword" placeholder="Password" required />
                        </div>
                        <button type="submit" className="btn btn-primary btn-block mt-3">Signup</button>
                    </form>
                )}
            </div>
        );
    }
}

export default LoginSignup;