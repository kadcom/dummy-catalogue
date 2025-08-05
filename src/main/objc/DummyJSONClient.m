#import "DummyJSONClient.h"
#import "DJProduct.h"

static NSString * const kDummyJSONBaseURL = @"https://dummyjson.com";

@interface DummyJSONClient ()
@property (nonatomic, strong) NSURLSession *session;
@end

@implementation DummyJSONClient

- (instancetype)initWithSession:(NSURLSession *)session {
    self = [super init];
    if (self) {
        _session = session ?: [NSURLSession sharedSession];
    }
    return self;
}

- (instancetype)init {
    return [self initWithSession:nil];
}

- (NSURLRequest *)requestWithPath:(NSString *)path {
    NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@", kDummyJSONBaseURL, path]];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    request.HTTPMethod = @"GET";
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [request setValue:@"DummyJSONClient-ObjC/1.0" forHTTPHeaderField:@"User-Agent"];
    return request;
}

- (void)performRequest:(NSURLRequest *)request completionHandler:(void (^)(NSDictionary *jsonResponse, NSError *error))completion {
    __weak typeof(self) weakSelf = self;
    NSURLSessionDataTask *task = [self.session dataTaskWithRequest:request 
                                                 completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (!strongSelf) return;
        
        if (error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(nil, error);
            });
            return;
        }
        
        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        if (httpResponse.statusCode < 200 || httpResponse.statusCode >= 300) {
            NSError *httpError = [NSError errorWithDomain:@"DummyJSONClientError" 
                                                     code:httpResponse.statusCode 
                                                 userInfo:@{NSLocalizedDescriptionKey: [NSString stringWithFormat:@"HTTP Error %ld", (long)httpResponse.statusCode]}];
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(nil, httpError);
            });
            return;
        }
        
        if (!data) {
            NSError *noDataError = [NSError errorWithDomain:@"DummyJSONClientError" 
                                                       code:-1 
                                                   userInfo:@{NSLocalizedDescriptionKey: @"No data received"}];
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(nil, noDataError);
            });
            return;
        }
        
        NSError *jsonError;
        id jsonObject = [NSJSONSerialization JSONObjectWithData:data options:0 error:&jsonError];
        
        if (jsonError || ![jsonObject isKindOfClass:[NSDictionary class]]) {
            dispatch_async(dispatch_get_main_queue(), ^{
                completion(nil, jsonError ?: [NSError errorWithDomain:@"DummyJSONClientError" 
                                                                 code:-2 
                                                             userInfo:@{NSLocalizedDescriptionKey: @"Invalid JSON response"}]);
            });
            return;
        }
        
        dispatch_async(dispatch_get_main_queue(), ^{
            completion((NSDictionary *)jsonObject, nil);
        });
    }];
    
    [task resume];
}

- (void)getProduct:(NSInteger)productId completion:(DJProductCompletionHandler)completion {
    NSString *path = [NSString stringWithFormat:@"/products/%ld", (long)productId];
    NSURLRequest *request = [self requestWithPath:path];
    
    [self performRequest:request completionHandler:^(NSDictionary *jsonResponse, NSError *error) {
        if (error) {
            completion(nil, error);
            return;
        }
        
        DJProduct *product = [[DJProduct alloc] initWithDictionary:jsonResponse];
        completion(product, nil);
    }];
}

- (void)getProductsWithLimit:(NSInteger)limit skip:(NSInteger)skip completion:(DJProductsCompletionHandler)completion {
    NSString *path = [NSString stringWithFormat:@"/products?limit=%ld&skip=%ld", (long)limit, (long)skip];
    NSURLRequest *request = [self requestWithPath:path];
    
    [self performRequest:request completionHandler:^(NSDictionary *jsonResponse, NSError *error) {
        if (error) {
            completion(nil, error);
            return;
        }
        
        NSArray *productsArray = jsonResponse[@"products"];
        if (![productsArray isKindOfClass:[NSArray class]]) {
            NSError *parseError = [NSError errorWithDomain:@"DummyJSONClientError" 
                                                      code:-3 
                                                  userInfo:@{NSLocalizedDescriptionKey: @"Invalid products array"}];
            completion(nil, parseError);
            return;
        }
        
        NSMutableArray *products = [NSMutableArray arrayWithCapacity:productsArray.count];
        for (NSDictionary *productDict in productsArray) {
            if ([productDict isKindOfClass:[NSDictionary class]]) {
                DJProduct *product = [[DJProduct alloc] initWithDictionary:productDict];
                [products addObject:product];
            }
        }
        
        completion([products copy], nil);
    }];
}

- (void)getAllProductsWithCompletion:(DJProductsCompletionHandler)completion {
    [self getProductsWithLimit:100 skip:0 completion:completion];
}

- (void)searchProducts:(NSString *)query completion:(DJProductsCompletionHandler)completion {
    NSString *encodedQuery = [query stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
    NSString *path = [NSString stringWithFormat:@"/products/search?q=%@", encodedQuery];
    NSURLRequest *request = [self requestWithPath:path];
    
    [self performRequest:request completionHandler:^(NSDictionary *jsonResponse, NSError *error) {
        if (error) {
            completion(nil, error);
            return;
        }
        
        NSArray *productsArray = jsonResponse[@"products"];
        if (![productsArray isKindOfClass:[NSArray class]]) {
            NSError *parseError = [NSError errorWithDomain:@"DummyJSONClientError" 
                                                      code:-3 
                                                  userInfo:@{NSLocalizedDescriptionKey: @"Invalid products array"}];
            completion(nil, parseError);
            return;
        }
        
        NSMutableArray *products = [NSMutableArray arrayWithCapacity:productsArray.count];
        for (NSDictionary *productDict in productsArray) {
            if ([productDict isKindOfClass:[NSDictionary class]]) {
                DJProduct *product = [[DJProduct alloc] initWithDictionary:productDict];
                [products addObject:product];
            }
        }
        
        completion([products copy], nil);
    }];
}

@end