import { useState, useEffect } from "react";

const API = "http://localhost:8080";

function App() {
  const [rideId, setRideId] = useState("");
  const [rideStatus, setRideStatus] = useState(null);
  const [tripId, setTripId] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

   const pickupLat = 28.60 + Math.random() * 0.05;
          const pickupLng = 77.20 + Math.random() * 0.05;

          const dropLat = 28.50 + Math.random() * 0.05;
          const dropLng = 77.30 + Math.random() * 0.05;
  const getStatusColor = (status) => {
    switch (status) {
      case "REQUESTED": return "#6c757d";
      case "OFFERED": return "#007bff";
      case "ACCEPTED": return "#fd7e14";
      case "ONGOING": return "#6f42c1";
      case "COMPLETED": return "#28a745";
      default: return "#000";
    }
  };

  const createRide = async () => {
    try {
      setLoading(true);
      setError("");
      setRideStatus(null);
      setTripId("");

      const riderId = "rider-" + crypto.randomUUID().substring(0, 5);
      const distance = Math.floor(Math.random() * 20) + 5;

      const response = await fetch(`${API}/v1/rides`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Idempotency-Key": crypto.randomUUID()
        },


        body: JSON.stringify({
          riderId,
          distance,
          pickupLocation: {
            latitude: pickupLat,
            longitude: pickupLng
          },
          dropLocation: {
            latitude: dropLat,
            longitude: dropLng
          }
        })
      });

      const data = await response.json();
      if (!response.ok) throw new Error(JSON.stringify(data));

      setRideId(data.rideId);
      setRideStatus(data);

    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const fetchRide = async (id) => {
    try {
      const response = await fetch(`${API}/v1/rides/${id}`);
      if (!response.ok) throw new Error("Failed to fetch ride");
      const data = await response.json();
      setRideStatus(data);
    } catch (err) {
      setError(err.message);
    }
  };

  const acceptRide = async () => {
    try {
      const response = await fetch(
        `${API}/v1/drivers/${rideStatus.driverId}/accept`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ rideId })
        }
      );

      if (!response.ok) throw new Error("Accept failed");
      const data = await response.json().catch(() => null);
      if (data?.tripId) setTripId(data.tripId);

    } catch (err) {
      setError(err.message);
    }
  };

  const startTrip = async () => {
    try {
      await fetch(`${API}/v1/trips/${tripId}/start`, { method: "POST" });
    } catch (err) {
      setError(err.message);
    }
  };

  const endTrip = async () => {
    try {
      await fetch(`${API}/v1/trips/${tripId}/end`, { method: "POST" });
    } catch (err) {
      setError(err.message);
    }
  };

  const pay = async () => {
    try {
      if (!rideStatus.finalFare) {
        alert("Trip not completed yet");
        return;
      }

      const response = await fetch(`${API}/v1/payments`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Idempotency-Key": crypto.randomUUID()
        },
        body: JSON.stringify({
          rideId,
          amount: rideStatus.finalFare
        })
      });

      if (!response.ok) {
        const err = await response.text();
        throw new Error(err);
      }

      alert("Payment Successful");

    } catch (err) {
      setError(err.message);
    }
  };

  useEffect(() => {
    if (!rideId) return;
    const interval = setInterval(() => {
      fetchRide(rideId);
    }, 2000);
    return () => clearInterval(interval);
  }, [rideId]);

  return (
    <div style={{ padding: 40, fontFamily: "Arial", maxWidth: 600, margin: "auto" }}>
      <h2>Ride Hailing Demo</h2>

      <button
        onClick={createRide}
        disabled={loading}
        style={{ padding: "10px 20px", marginTop: 10 }}
      >
        {loading ? "Creating..." : "Create Ride"}
      </button>

      {error && (
        <p style={{ color: "red", marginTop: 10 }}>
          Error: {error}
        </p>
      )}

      {rideId && (
        <div style={{ marginTop: 20 }}>
          <strong>Ride ID:</strong> {rideId}
        </div>
      )}

      {rideStatus && (
        <div
          style={{
            marginTop: 20,
            padding: 20,
            border: "1px solid #ddd",
            borderRadius: 8
          }}
        >
          <h3 style={{ color: getStatusColor(rideStatus.status) }}>
            Status: {rideStatus.status}
          </h3>

          <p><strong>Driver:</strong> {rideStatus.driverId || "Not Assigned"}</p>
          <p><strong>Estimated Fare:</strong> ₹{rideStatus.estimatedFare}</p>
          <p><strong>Final Fare:</strong> {rideStatus.finalFare ? `₹${rideStatus.finalFare}` : "-"}</p>

          {rideStatus.status === "OFFERED" && (
            <button onClick={acceptRide} style={{ marginTop: 10 }}>
              Accept Ride
            </button>
          )}

          {rideStatus.status === "ACCEPTED" && (
            <button onClick={startTrip} style={{ marginTop: 10 }}>
              Start Trip
            </button>
          )}

          {rideStatus.status === "ONGOING" && (
            <button onClick={endTrip} style={{ marginTop: 10 }}>
              End Trip
            </button>
          )}

          {rideStatus.status === "COMPLETED" && (
            <button onClick={pay} style={{ marginTop: 10 }}>
              Pay
            </button>
          )}
        </div>
      )}
    </div>
  );
}

export default App;