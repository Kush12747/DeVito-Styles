import "../../styles/about.css";

function NotFound() {
    return (
        <div className="notfound-page">
            <p className="notfound-eyebrow">Error 404</p>
            <h1 className="notfound-title">404 Not Found</h1>
            <div className="notfound-divider"></div>
            <p className="notfound-text">The page does not exist</p>
        </div>
    );
}

export default NotFound;
