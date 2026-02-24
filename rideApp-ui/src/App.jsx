import { useState, useEffect, useRef } from "react";
import { Client } from "@stomp/stompjs";

const WS_URL = "ws://localhost:8080/ws";
const API = "http://localhost:8080";

function App() {
  const [rides, setRides] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [connected, setConnected] = useState(false);
  const stompClientRef = useRef(null);

  useEffect(() => {
    const client = new Client({
      brokerURL: WS_URL,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("Connected");
        setConnected(true);
      },
      onDisconnect: () => {
        setConnected(false);
      },
      onStompError: (frame) => {
        console.error("Broker error:", frame);
      }
    });

    client.activate();
    stompClientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, []);

 const subscribeToRide = (rideId) => {
     if (!stompClientRef.current || !connected) {
         console.log("⏳ WebSocket not ready yet");
         return;
       }

     stompClientRef.current.subscribe(
       `/topic/rides/${rideId}`,
       (message) => {
         const updatedRide = JSON.parse(message.body);

         setRides((prev) =>
           prev.map(r =>
             r.rideId === updatedRide.rideId ? updatedRide : r
           )
         );
     console.log("📩 WebSocket message received:", message.body);
       }
     );
   };

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

  const createRide = async (ride) => {
    try {
      setLoading(true);
      setError("");

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
            latitude: 28.60 + Math.random() * 0.05,
            longitude: 77.20 + Math.random() * 0.05
          },
          dropLocation: {
            latitude: 28.50 + Math.random() * 0.05,
            longitude: 77.30 + Math.random() * 0.05
          }
        })
      });

      const data = await response.json();
      if (!response.ok) throw new Error(JSON.stringify(data));

    setRides(prev => [...prev, data]);
    subscribeToRide(data.rideId);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

 const acceptRide = async (ride) => {
   try {
     const response = await fetch(
       `${API}/v1/drivers/${ride.driverId}/accept`,
       {
         method: "POST",
         headers: { "Content-Type": "application/json" },
         body: JSON.stringify({ rideId: ride.rideId })
       }
     );

     const data = await response.json().catch(() => null);

     setRides(prev =>
       prev.map(r =>
         r.rideId === ride.rideId
           ? { ...r, tripId: data?.tripId, status: "ACCEPTED" }
           : r
       )
     );

   } catch (err) {
     setError(err.message);
   }
 };

  const startTrip = async (ride) => {
    try {

      const response = await fetch(`${API}/v1/trips/${ride.tripId}/start`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        }
      });

      if (response.ok) {
            setRides(prev =>
              prev.map(r =>
                r.rideId === ride.rideId
                  ? { ...r, status: "ONGOING" }
                  : r
              )
            );
          }

    } catch (error) {
      console.error("Error starting trip:", error);
    }
  };

  const endTrip = async (ride) => {
     try {
        const response = await fetch(
          `${API}/v1/trips/${ride.tripId}/end`,
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json"
            }
          }
        );

        if (response.ok) {
          setRides(prev =>
            prev.map(r =>
              r.rideId === ride.rideId
                ? { ...r, status: "COMPLETED" }
                : r
            )
          );
        }

      } catch (err) {
        console.error(err);
      }
  };

  const pay = async () => {
    try {
      if (!ride.finalFare) {
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
          amount: ride.finalFare
        })
      });

      if (!response.ok) {
        const err = await response.text();
        throw new Error(err);
      }

      setRides(prev =>
            prev.map(r =>
              r.rideId === ride.rideId
                ? { ...r, status: "PAID" }
                : r
            )
          );

    } catch (err) {
      console.error(err);
    }
  };


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

      {rides.map((ride) => (
           <div
             key={ride.rideId}
             style={{
               marginTop: 20,
               padding: 20,
               border: "1px solid #ddd",
               borderRadius: 8
             }}
           >
             <h3 style={{ color: getStatusColor(ride.status) }}>
               Ride ID: {ride.rideId}
             </h3>

             <p>Status: {ride.status}</p>
             <p>Driver: {ride.driverId || "Not Assigned"}</p>
             <p>Estimated Fare: ₹{ride.estimatedFare}</p>
             <p>Final Fare: {ride.finalFare ? `₹${ride.finalFare}` : "-"}</p>

             {ride.status === "OFFERED" && (
               <button onClick={() => acceptRide(ride)}>
                 Accept Ride
               </button>
             )}

             {ride.status === "ACCEPTED" && (
               <button onClick={() => startTrip(ride)}>
                 Start Trip
               </button>
             )}

             {ride.status === "ONGOING" && (
               <button onClick={() => endTrip(ride)}>
                 End Trip
               </button>
             )}

             {ride.status === "COMPLETED" && (
               <button onClick={() => alert("Payment Gateway")}>
                 Pay
               </button>
             )}
           </div>
         ))}
       </div>
     );

 }
export default App;