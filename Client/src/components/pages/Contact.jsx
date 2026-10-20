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
        <section className="contact-page">
            <div className="contact-inner">
            
                {/* Left Side */}
                <div className="contact-info">

                    <p className="contact-eyebrow">Contact</p>
                
                    <h2 className="contact-title">
                        <span className="contact-title-line">Book Your</span>
                        <span className="contact-title-line contact-title-accent">Next Cut</span>
                    </h2>

                    <div className="contact-divider"></div>

                    <p className="contact-description">
                        Whether you're looking for a sharp fade,
                        beard cleanup, or a full transformation,
                        DeVito Styles delivers premium grooming
                        with precision and style.
                    </p>

                    <div className="contact-details">
                        <div className="contact-detail">
                            <h3>Location</h3>
                            <p>Miami, Florida</p>
                        </div>

                        <div className="contact-detail">
                            <h3>Phone</h3>
                            <p>(847) 555-1234</p>
                        </div>

                        <div className="contact-detail">
                            <h3>Hours</h3>
                            <p>Mon - Sat: 9AM - 4PM</p>
                        </div>
                    </div>
                </div>

                {/* Right Side */}
                <div className="contact-form-wrap">

                    <form onSubmit={handleSubmit} className="contact-form">

                        <p className="contact-eyebrow">Enquiries</p>
                        <h2 className="contact-form-title">Send A Message</h2>

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

                        <button type="submit" className="contact-submit">Send Message</button>

                        {submitted && (<div className="contact-success">✓ Message submitted successfully.</div>)}

                    </form>
                </div>
            </div>
        </section>
    );
}

export default Contact;