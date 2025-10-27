<div align="center">
  <img src="https://github.com/user-attachments/assets/2e944381-d236-4ca8-81ff-db648950e088" alt="Nurad_Admin_Logo" width="700" />
</div>

# 🏨 Nurad Admin: Mobile Hotel Management System

## ✨ Project Overview

Nurad Admin is an Android application designed to provide hotel administrators with a platform for managing all  hotel operations. 💻📊 

It facilitates efficient oversight of room inventory, guest bookings, and real-time data synchronization. Developed using Android Studio and Java, with Firebase as the backend, this system empowers hotel staff with the tools for effective management and operational control.

## 🌟 Key Features

Explore the core administrative functionalities of Nurad Admin:

*   **Centralized Dashboard:** Offers an aggregated view of key operational metrics and quick access to essential management features.
*   **Booking Management:** Enables administrators to perform CRUD (Create, Read, Update, Delete) operations on guest bookings, including reservation details, check-in/check-out statuses, and guest information.
*   **Room Inventory Management:** Provides real-time visibility into room status (occupied, vacant, pending cleaning), configuration of room types, and capacity allocation.
*   **Real-time Data Synchronization:** Leverages Firebase Realtime Database to ensure immediate propagation of all operational data changes across the system, maintaining data consistency.
*   **Secure Authentication:** Implements Firebase Authentication for secure administrator login.
  
## 🛠️ Technologies Utilized

Nurad Admin is constructed upon the following core technologies:

*   **Android Studio:** The official Integrated Development Environment (IDE) for native Android application development.
*   **Java:** The core programming language employed for implementing application logic, UI components, and integrating with backend services.
*   **Firebase:** Utilized as the Backend as a Service (BaaS) platform, providing:
    *   **Firebase Realtime Database:** For efficient, synchronous, and scalable data storage and retrieval of hotel operational data.
    *   **Firebase Authentication:** For robust and secure administrator user management and identity verification.

## 🚀 Getting Started

These instructions detail the process of setting up and running the Nurad Admin application on a local development environment.

### 📋 Prerequisites

*   Android Studio (Latest Stable Version Recommended)
*   Java Development Kit (JDK) 8 or higher
*   A configured Google Firebase project with:
    *   Firebase Realtime Database enabled.
    *   Firebase Authentication (Email/Password provider) enabled.

### ⬇️ Installation

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/angelb9967/Nurad-Admin-Mobile.git
    ```
2.  **Open Project in Android Studio:**
    *   Launch Android Studio.
    *   Select `Open an existing Android Studio project` and navigate to the root directory of the cloned `NUrad-Admin` repository.
3.  **Verify Firebase Configuration:**
    *   Ensure the `google-services.json` file is correctly placed within the `app/` directory of the Android project.
    *   Confirm that necessary Firebase SDK dependencies are accurately declared in both the project-level `build.gradle` and app-level `build.gradle` files.
4.  **Synchronize Gradle:**
    *   Allow Android Studio to perform a Gradle synchronization. Resolve any dependency resolution issues that may arise.

## ▶️ Usage

Post-installation, the Nurad Admin application can be operated as follows:

1.  **Application Execution:**
    *   Connect an Android physical device with USB debugging enabled or launch an Android Virtual Device (AVD) via the Android Studio AVD Manager.
    *   Initiate the application build and run process by selecting the `Run 'app'` command within Android Studio.
2.  **Administrator Login:**
    *   Upon the application's initial launch, an authentication interface will be presented.
    *   Enter valid administrator credentials, which must be pre-registered within your Firebase Authentication service, to gain access to the system's management functionalities.
3.  **Operational Management:**
    *   Navigate through the administrative dashboard and integrated modules to actively manage bookings, monitor room availability, and interact with the real-time data feeds to maintain hotel operations.

## 📧 Contact

For any inquiries or feedback regarding this project, please reach out to the team:

| Name               | Email                                    |
| :----------------- | :--------------------------------------- |
| Allysandrei | 👨‍🎨 [allysandreiaparicio](https://github.com/allysandreiaparicio) |
| Angeline     | 👩‍💻 [angelb9967](https://github.com/angelb9967) |
| Angie        | 👩‍🎨 [anjicln ](https://github.com/anjicln) |
| Jersey       | 👩‍💻 [jerseyloveu](https://github.com/jerseyloveu) |

Project Repository: [https://github.com/angelb9967/Nurad-Admin-Mobile.git](https://github.com/angelb9967/Nurad-Admin-Mobile.git)
