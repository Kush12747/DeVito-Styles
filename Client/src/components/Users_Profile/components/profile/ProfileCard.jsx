import "../../../../styles/profile.css";

function ProfileCard({user, setEditMode, editMode, handleImageUpload}) {

  if (!user) {
    return null;
  }

  return (
    <div className="profile-card">

      <div className="profile-header">

        <div className="image-upload-container">

          <label htmlFor="profile-upload">

            <img
              src={
                user.profileUrl ||
                "https://placehold.co/150x150"
              }
              alt={`${user.firstName} ${user.lastName}`}
              className="profile-image"
            />

          </label>

          <input
            id="profile-upload"
            type="file"
            accept="image/*"
            onChange={(e) => handleImageUpload(e.target.files[0])}
            hidden
          />

        </div>

        <div className="profile-info">

          <h2>
            {user.firstName} {user.lastName}
          </h2>

          <p>
            Email: {user.email}
          </p>

          <p>
            Phone: {user.phone}
          </p>

        </div>

      </div>

      <button
        className="edit-btn"
        onClick={() => setEditMode(true)}
      >
        Edit Profile
      </button>

    </div>
  );
}

export default ProfileCard;