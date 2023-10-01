import React, { Component } from 'react';
import '../styles/LoginSignup.css';
import {MDBBtn, MDBInput, MDBInputGroup} from "mdb-react-ui-kit";
import {FaEye, FaEyeSlash} from "react-icons/fa";


class LoginSignup extends Component {
    constructor(props) {
        super(props);
        this.state = {
            activeTab: 'login',
            showPassword: false, // for password visibility
        };
    }

    handleTabChange = (tab) => {
        this.setState({ activeTab: tab, showPassword: false });
    };

    render() {
        const { activeTab, showPassword } = this.state;

        return (

            <div className="login-container">
                <img src="/img/logo-bus.png" alt="Welcome Image" className="welcome-image" />
                <p className="logo-paragraph">Plan your bus trips with ease! </p>

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
                            <div className="password-input">
                                <input
                                    type={showPassword ? 'text' : 'password'} // Use showPassword from state
                                    className="form-control password-input-field" // Add a unique class name
                                    id="loginPassword"
                                    placeholder="Password"
                                    required
                                />
                                <MDBBtn
                                    floating
                                    color='info'
                                    wrapperClass='float-start'
                                    onClick={() => this.setState({ showPassword: !showPassword })}
                                    className="show-password-button"
                                    type="button"
                                >
                                    {showPassword ? <FaEye /> : <FaEyeSlash />}
                                </MDBBtn>
                            </div>
                        </div>

                        <div className="form-group forgot-password">
                            <a href="#" className="text-forget-password">Forgot password?</a>
                        </div>
                        <div className="form-group">
                            <button type="submit" className="btn btn-primary btn-block">Login</button>
                        </div>
                        <p className="text-center mt-3"><span className="small-text">Not a member?</span> <a href="#" data-toggle="tab"><span className="text-signup-now">Signup now</span></a></p>
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
                            <div className="password-input">
                                <input
                                    type={showPassword ? 'text' : 'password'} // Use showPassword from state
                                    className="form-control signup-password-input" // Add a unique class name
                                    id="signupPassword"
                                    placeholder="Password"
                                    required
                                />
                                <MDBBtn
                                    floating
                                    color='info'
                                    wrapperClass='float-start'
                                    onClick={() => this.setState({ showPassword: !showPassword })}
                                    className="show-password-button"
                                    type="button"
                                >
                                    {showPassword ? <FaEye /> : <FaEyeSlash />}
                                </MDBBtn>
                            </div>
                        </div>

                        <div className="form-group">
                        <button type="submit" className="btn btn-primary btn-block mt-3">Signup</button>
                        </div>
                    </form>
                )}
            </div>
        );
    }
}

export default LoginSignup;