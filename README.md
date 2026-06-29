# 🚨 Beacon

**Beacon** is a disaster management platform that enables emergency communication even when traditional communication infrastructure fails. Using **Bluetooth Mesh Networking**, Beacon allows users to relay SOS messages between nearby devices until they reach a device with internet connectivity, ensuring critical information reaches emergency responders.

## 🌍 Why Beacon?

During natural disasters, cellular networks and internet services often become unavailable, making it difficult for people to request help. Beacon addresses this problem by creating an offline communication network using nearby smartphones.

## ✨ Features

- 📡 Offline communication using Bluetooth Mesh
- 🆘 One-tap SOS alerts
- 📍 GPS location sharing
- 🔄 Multi-hop message forwarding
- 🌐 Automatic cloud synchronization when internet is available
- 🗺️ Live emergency dashboard for rescue teams
- ⚡ Designed for disaster and emergency situations

## 🛠️ Tech Stack

### Mobile
- Flutter
- Bluetooth Low Energy (BLE)

### Backend
- Node.js
- Express.js

### Database
- MongoDB

### Dashboard
- React.js

### Maps
- Google Maps API

## 🚀 How It Works

1. A user sends an SOS request.
2. The SOS message is broadcast to nearby devices using Bluetooth.
3. Each device forwards the message to the next nearby device, creating a Bluetooth mesh network.
4. Once any device in the network gains internet access, the SOS request is synchronized with the server.
5. Rescue teams receive the alert through a live web dashboard and can coordinate emergency response.

## 🎯 Project Goals

- Enable communication without internet or cellular networks.
- Improve emergency response during disasters.
- Provide real-time visibility for rescue teams.
- Build a scalable and reliable disaster communication system.

---

Built with ❤️ to make emergency communication accessible when it matters most.
