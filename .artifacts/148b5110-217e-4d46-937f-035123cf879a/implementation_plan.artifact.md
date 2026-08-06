# Refactor DetailClientRepository to consolidate functions

Consolidate `getInfoClient`, `getPatientsForClient`, and the existing `getClientWithPatients` into a single function `getClientWithPatients(clientId: String, isLocal: Boolean)`.

## Proposed Changes

### Domain Layer

#### [MODIFY] [DetailClientRepository.kt](file:///C:/Users/yosel/AndroidStudioProjects/Atti/app/src/main/java/yosel/dev/atti/screens/detail_client/domain/DetailClientRepository.kt)
- Remove `getInfoClient` and `getPatientsForClient`.
- Update `getClientWithPatients` to accept `isLocal: Boolean`.

### Data Layer

#### [MODIFY] [DetailClientRepositoryImpl.kt](file:///C:/Users/yosel/AndroidStudioProjects/Atti/app/src/main/java/yosel/dev/atti/screens/detail_client/data/DetailClientRepositoryImpl.kt)
- Implement the consolidated logic in `getClientWithPatients`.
- Use `isLocal` to decide whether to fetch patients from remote and sync with Room.
- Ensure the client exists in Room before proceeding with remote patient fetching.

## Verification Plan

### Manual Verification
- Verify that calling `getClientWithPatients` with `isLocal = true` returns data only from Room.
- Verify that calling `getClientWithPatients` with `isLocal = false` fetches from remote, saves to Room, and returns the updated model.
- Check for proper error handling (e.g., client not found in Room).
