function EditProfileForm({ formData, handleChange, handleSubmit, setEditMode }) {
    return (
        <div className="edit-form-card">
            <h3>Edit Profile</h3>

            <form onSubmit={handleSubmit}>

                <input type="text" name="firstName" placeholder="First Name" value={formData.firstName} onChange={handleChange} />

                <input type="text" name="lastName" placeholder="Last Name" value={formData.lastName} onChange={handleChange} />

                <input type="email" name="email" placeholder="Email" value={formData.email} onChange={handleChange} />

                <input type="text" name="phone" placeholder="Phone" value={formData.phone} onChange={handleChange} />

                <div className="form-buttons">
                    <button type="submit">Save Changes</button>
                    <button type="button" onClick={() => setEditMode(false)}>Cancel</button>
                </div>
            </form>
        </div>  
    )
}

export default EditProfileForm;