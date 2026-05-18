import "../../styles/profile.css";

function ProfileCard({ user, setEditMode, profileImage, handleImageUpload }) {
    if (!user) return null;
    
    return (
        <div className="profile-card">
            <div className="profile-header">
                <div className="image-upload-container">
                    <label htmlFor="profile-upload">

                        <img src={profileImage || "https://placehold.co/150x150"} alt="profile" className="profile-image" />

                    </label>

                    <input id="profile-upload" type="file" accept="image/*" onChange={handleImageUpload} hidden />
                </div>

                <div className="profile-info">
                    <h2>
                        {user?.firstName} {user?.lastName}
                    </h2>

                    <p>{user?.email}</p>

                    <p>{user?.phone}</p>

                </div>

            </div>

            <button className="edit-btn" onClick={() => setEditMode(true)}>Edit Profile</button>

        </div>
    )
}

export default ProfileCard;