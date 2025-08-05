#import <XCTest/XCTest.h>
#import "src/main/objc/DummyJSONClient.h"
#import "src/main/objc/DJProduct.h"

@interface DummyJSONClientTests : XCTestCase
@property (nonatomic, strong) DummyJSONClient *client;
@end

@implementation DummyJSONClientTests

- (void)setUp {
    [super setUp];
    self.client = [[DummyJSONClient alloc] init];
}

- (void)tearDown {
    self.client = nil;
    [super tearDown];
}

- (void)testGetSingleProduct {
    XCTestExpectation *expectation = [self expectationWithDescription:@"Get single product"];
    
    [self.client getProduct:1 completion:^(DJProduct *product, NSError *error) {
        XCTAssertNil(error, @"Error should be nil");
        XCTAssertNotNil(product, @"Product should not be nil");
        XCTAssertEqual(product.productId, 1, @"Product ID should be 1");
        XCTAssertNotNil(product.title, @"Product title should not be nil");
        XCTAssertGreaterThan(product.price, 0, @"Product price should be greater than 0");
        
        [expectation fulfill];
    }];
    
    [self waitForExpectationsWithTimeout:10.0 handler:nil];
}

- (void)testGetAllProducts {
    XCTestExpectation *expectation = [self expectationWithDescription:@"Get all products"];
    
    [self.client getAllProductsWithCompletion:^(NSArray<DJProduct *> *products, NSError *error) {
        XCTAssertNil(error, @"Error should be nil");
        XCTAssertNotNil(products, @"Products should not be nil");
        XCTAssertGreaterThan(products.count, 0, @"Should have at least one product");
        
        DJProduct *firstProduct = products.firstObject;
        XCTAssertNotNil(firstProduct.title, @"First product should have a title");
        XCTAssertGreaterThan(firstProduct.price, 0, @"First product should have a price > 0");
        
        [expectation fulfill];
    }];
    
    [self waitForExpectationsWithTimeout:10.0 handler:nil];
}

- (void)testSearchProducts {
    XCTestExpectation *expectation = [self expectationWithDescription:@"Search products"];
    
    [self.client searchProducts:@"phone" completion:^(NSArray<DJProduct *> *products, NSError *error) {
        XCTAssertNil(error, @"Error should be nil");
        XCTAssertNotNil(products, @"Products should not be nil");
        XCTAssertGreaterThan(products.count, 0, @"Should find at least one phone product");
        
        [expectation fulfill];
    }];
    
    [self waitForExpectationsWithTimeout:10.0 handler:nil];
}

- (void)testProductHelperMethods {
    XCTestExpectation *expectation = [self expectationWithDescription:@"Test product helper methods"];
    
    [self.client getProduct:1 completion:^(DJProduct *product, NSError *error) {
        XCTAssertNil(error, @"Error should be nil");
        XCTAssertNotNil(product, @"Product should not be nil");
        
        double discountedPrice = [product discountedPrice];
        XCTAssertLessThan(discountedPrice, product.price, @"Discounted price should be less than original price");
        
        BOOL inStock = [product isInStock];
        XCTAssertEqual(inStock, product.stock > 0, @"Stock status should match stock count");
        
        NSString *displayPrice = [product displayPrice];
        XCTAssertNotNil(displayPrice, @"Display price should not be nil");
        XCTAssertTrue([displayPrice hasPrefix:@"$"], @"Display price should start with $");
        
        [expectation fulfill];
    }];
    
    [self waitForExpectationsWithTimeout:10.0 handler:nil];
}

- (void)testNonExistentProduct {
    XCTestExpectation *expectation = [self expectationWithDescription:@"Get non-existent product"];
    
    [self.client getProduct:99999 completion:^(DJProduct *product, NSError *error) {
        XCTAssertNotNil(error, @"Error should not be nil for non-existent product");
        XCTAssertNil(product, @"Product should be nil for non-existent product");
        
        [expectation fulfill];
    }];
    
    [self waitForExpectationsWithTimeout:10.0 handler:nil];
}

@end