// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "LagowareCapacitorSqlite",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "LagowareCapacitorSqlite",
            targets: ["SqlitePlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "SqlitePlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/SqlitePlugin"),
        .testTarget(
            name: "SqlitePluginTests",
            dependencies: ["SqlitePlugin"],
            path: "ios/Tests/SqlitePluginTests")
    ]
)