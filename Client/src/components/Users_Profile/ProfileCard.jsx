import "../../styles/profile.css";

function ProfileCard({ user, setEditMode, handleImageUpload }) {
    if (!user) return null;
    
    return (
        <div className="profile-card">
            <div className="profile-header">
                <div className="image-upload-container">
                    <label htmlFor="profile-upload">

                        <img src={user?.profileUrl || "https://placehold.co/150x150"} alt="profile" className="profile-image" />

                    </label>

                    <input id="profile-upload" type="file" accept="image/*" onChange={handleImageUpload} hidden />
                </div>

                <div className="profile-info">
                    <span className="profile-eyebrow">Member Profile</span>

                    <h2>
                        {user?.firstName} {user?.lastName}
                    </h2>

                    <p><span className="profile-label">Email</span>{user?.email}</p>

                    <p><span className="profile-label">Phone</span>{user?.phone}</p>

                </div>

            </div>

            <button className="edit-btn" onClick={() => setEditMode(true)}>Edit Profile</button>

        </div>
    )
}

export default ProfileCard;