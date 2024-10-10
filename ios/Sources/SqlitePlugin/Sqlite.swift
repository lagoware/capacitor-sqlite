import Foundation

@objc public class Sqlite: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
