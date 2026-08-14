//package net.engineeringdigest.journalApp.ServiceTest;
//
//import org.junit.jupiter.api.extension.ExtensionContext;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.ArgumentsProvider;
//
//import java.util.stream.Stream;
//
//public class ArgumentsProviderClass implements ArgumentsProvider {
//
//    @Override
//    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
//        return Stream.of(
//                Arguments.of("Afaq", "ez123"),
//                Arguments.of("Ezio", "assassin456"),
//                Arguments.of("", "shouldFail")   // edge case: blank username
//        );
//    }
//}