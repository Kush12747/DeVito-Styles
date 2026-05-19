import "../../styles/contact.css";
import { useState } from 'react';

function Contact() {

    const [formData, setFormData] = useState({
        name: "",
        email: "",
        phone: "",
        message: ""
    });

    const [submitted, setSubmitted] = useState(false);

    function handleChange(e) {
        setFormData({...formData, [e.target.name]: e.target.value});
    
    }

    function handleSubmit(e) {
        e.preventDefault();
        console.log(formData);

        setSubmitted(true);

        setFormData({
            name: "",
            email: "",
            phone: "",
            message: ""
        });

        // hide message after 4 sec
        setTimeout(() => {
            setSubmitted(false);
        }, 4000);
    }

    return (
        <section className="contact-section">
            
            {/* Left Side */}
            <div className="contact-info">

                <p className="contact-tag">CONTACT</p>
                
                <h2 className="contact-title">
                    <span className="line-one">Book Your</span>
                    <br />
                    <span className="line-two">Next Cut</span>
                </h2>

                <div className="gold-line"></div>

                <p className="contact-description">
                    Whether you're looking for a sharp fade,
                    beard cleanup, or a full transformation,
                    DeVito Styles delivers premium grooming
                    with precision and style.
                </p>

                <div className="info-cards">
                    <div className="info-card">
                        <h3>Location</h3>
                        <p>📍 Miami, Florida</p>
                    </div>

                    <div className="info-card">
                        <h3>Phone</h3>
                        <p>(847) 555-1234</p>
                    </div>

                    <div className="info-card">
                        <h3>Hours</h3>
                        <p>Mon - Sat: 9AM - 4PM</p>
                    </div>
                </div>
            </div>

            {/* Right Side */}
            <div className="form-wrapper">

                <form onSubmit={handleSubmit} className="contact-form">

                    <h2>Send A Message</h2>

                    <input
                        type="text"
                        name="name"
                        placeholder="Your Name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                    />

                    <input
                        type="email"
                        name="email"
                        placeholder="Your Email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />

                    <input
                        type="tel"
                        name="phone"
                        placeholder="Phone Number"
                        value={formData.phone}
                        onChange={handleChange}
                    />

                    <textarea
                        name="message"
                        placeholder="Tell us what you're looking for..."
                        rows="6"
                        value={formData.message}
                        onChange={handleChange}
                        required
                    />

                    <button type="submit">Send Message</button>

                    {submitted && (<div className="success-message">✓ Message submitted successfully.</div>)}

                </form>
            </div>
        </section>
    );
}

export default Contact;