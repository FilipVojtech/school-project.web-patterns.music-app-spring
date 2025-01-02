function toggleDetails(playlistId) {
    const detailsBox = document.getElementById(`details-${playlistId}`);
    if (detailsBox.classList.contains('hidden')) {
        detailsBox.classList.remove('hidden');
    } else {
        detailsBox.classList.add('hidden');
    }
}

async function fetchAndToggleDetails(playlistId) {
    const detailsBox = document.getElementById(`details-${playlistId}`);
    const songsList = document.getElementById(`songs-list-${playlistId}`);

    // If the details box is hidden, fetch and display the songs
    if (detailsBox.style.display === "none" || !detailsBox.style.display) {
        try {
            // Fetch songs from the server
            const response = await fetch(`/playlists/${playlistId}/songs`);
            const songs = await response.json();

            // Clear previous song list
            songsList.innerHTML = "";

            // Populate the list with fetched songs
            songs.forEach(song => {
                const listItem = document.createElement("li");
                listItem.textContent = `${song.title} by ${song.artist}`;
                songsList.appendChild(listItem);
            });

            // Show the details box
            detailsBox.style.display = "block";
        } catch (error) {
            console.error("Error fetching songs:", error);
        }
    } else {
        // If already visible, hide the details box
        detailsBox.style.display = "none";
    }
}

async function removeSongFromPlaylist(playlistId, songId) {
    try {
        const response = await fetch(`/playlists/${playlistId}/removeSong`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ playlistId, songId })
        });

        if (response.ok) {
            alert("Song removed successfully!");
            fetchAndToggleDetails(playlistId); // Refresh the playlist details
        } else {
            alert("Failed to remove the song.");
        }
    } catch (error) {
        console.error("Error removing song:", error);
    }
}