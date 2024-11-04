package unit.java.sdk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import static unit.java.sdk.CustomerTests.CreateBusinessCustomer;
import static unit.java.sdk.CustomerTests.CreateIndividualCustomer;
import static unit.java.sdk.TestHelpers.GenerateUnitApiClient;
import unit.java.sdk.api.UnitApi;
import unit.java.sdk.model.Account;
import unit.java.sdk.model.AccountEndOfDay;
import unit.java.sdk.model.AddAccountOwnersRequest;
import unit.java.sdk.model.BusinessCustomer;
import unit.java.sdk.model.CloseAccountRequest;
import unit.java.sdk.model.CloseAccountRequestData;
import unit.java.sdk.model.CloseAccountRequestDataAttributes;
import unit.java.sdk.model.CreateAccountRequest;
import unit.java.sdk.model.CreateAccountRequestData;
import unit.java.sdk.model.CreateCreditAccount;
import unit.java.sdk.model.CreateCreditAccountAttributes;
import unit.java.sdk.model.CreateCreditAccountRelationships;
import unit.java.sdk.model.CreateDepositAccount;
import unit.java.sdk.model.CreateDepositAccountAttributes;
import unit.java.sdk.model.CreateDepositAccountRelationships;
import unit.java.sdk.model.CreateWalletAccount;
import unit.java.sdk.model.CreateWalletAccountAttributes;
import unit.java.sdk.model.CreateWalletAccountRelationships;
import unit.java.sdk.model.CreditAccount;
import unit.java.sdk.model.Customer;
import unit.java.sdk.model.CustomerRelationship;
import unit.java.sdk.model.CustomerRelationshipData;
import unit.java.sdk.model.DepositAccount;
import unit.java.sdk.model.DepositAccountOwner;
import unit.java.sdk.model.DepositProduct;
import unit.java.sdk.model.FreezeAccountRequest;
import unit.java.sdk.model.FreezeAccountRequestData;
import unit.java.sdk.model.FreezeAccountRequestDataAttributes;
import unit.java.sdk.model.GetAccountBalanceHistoryFilterParameter;
import unit.java.sdk.model.Limits;
import unit.java.sdk.model.ListPageParameters;
import unit.java.sdk.model.RemoveAccountOwnersRequest;
import unit.java.sdk.model.RepaymentInformation;
import unit.java.sdk.model.UnitAccountBalanceHistoryResponse;
import unit.java.sdk.model.UnitAccountResponse;
import unit.java.sdk.model.UnitAccountResponseWithIncluded;
import unit.java.sdk.model.UnitAccountsListResponse;
import unit.java.sdk.model.UnitDepositAccountResponse;
import unit.java.sdk.model.UnitDepositProductsResponse;
import unit.java.sdk.model.UnitGetAccountLimitsResponse;
import unit.java.sdk.model.UnitRepaymentInformationResponse;
import unit.java.sdk.model.UpdateAccountRequest;
import unit.java.sdk.model.UpdateAccountRequestData;
import unit.java.sdk.model.UpdateDepositAccount;
import unit.java.sdk.model.UpdateDepositAccountAttributes;
import unit.java.sdk.model.WalletAccount;

public class AccountTests {
    UnitApi unitApi = GenerateUnitApiClient();

    @Test
    public void GetAccountListApiTest() throws ApiException {
        UnitAccountsListResponse response = unitApi.getAccountsList(null, null, null);
        assert !response.getData().isEmpty();
    }

    @Test
    public void GetAccountApiTest() throws ApiException {
        UnitAccountsListResponse response = unitApi.getAccountsList(null, null, null);
        assert !response.getData().isEmpty();
        response.getData().forEach(x -> {
            try {
                UnitAccountResponseWithIncluded account = unitApi.getAccount(x.getId(), null);
                assert account.getData().getId().equals(x.getId());
                assert account.getData().getType().toString().toLowerCase()
                        .equals(account.getData().getClass().getSimpleName().toLowerCase());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    @Test
    public void UpdateAccountApiTest() throws ApiException {
        UnitAccountsListResponse response = unitApi.getAccountsList(null, null, null);
        assert !response.getData().isEmpty();
        response.getData().forEach(x -> {
            try {
                UnitAccountResponseWithIncluded account = unitApi.getAccount(x.getId(), null);
                assert account.getData().getId().equals(x.getId());
                assert account.getData().getType().toString().toLowerCase()
                        .equals(account.getData().getClass().getSimpleName().toLowerCase());

                UpdateDepositAccountAttributes attributes = new UpdateDepositAccountAttributes();
                HashMap<String, String> tags = new HashMap<>();
                tags.put("test", "open-api-sdk");
                attributes.tags(tags);

                UpdateDepositAccount updateDepositAccount = new UpdateDepositAccount();
                updateDepositAccount.attributes(attributes);
                UpdateAccountRequest pa = new UpdateAccountRequest().data(new UpdateAccountRequestData(updateDepositAccount));

                UnitAccountResponse res = unitApi.updateAccount(x.getId(), pa);
                res.getData().getId().equals(x.getId());
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static DepositAccount CreateDepositAccount(UnitApi unitApi, Customer customer) throws ApiException {
        CreateDepositAccount cda = new CreateDepositAccount();
        CreateDepositAccountAttributes attributes = new CreateDepositAccountAttributes();
        attributes.setDepositProduct("checking");

        CreateDepositAccountRelationships relationships = new CreateDepositAccountRelationships();
        CustomerRelationshipData customerRelationshipData = new CustomerRelationshipData();
        customerRelationshipData.setId(customer.getId());
        customerRelationshipData.setType(CustomerRelationshipData.TypeEnum.CUSTOMER);
        CustomerRelationship customerLinkageRelationship = new CustomerRelationship();
        customerLinkageRelationship.setData(customerRelationshipData);

        relationships.setCustomer(customerLinkageRelationship);

        cda.setAttributes(attributes);
        cda.setRelationships(relationships);

        CreateAccountRequest ca = new CreateAccountRequest();
        ca.setData(new CreateAccountRequestData(cda));


        DepositAccount depositAccount = (DepositAccount) unitApi.createAccount(ca).getData();
        assert depositAccount.getType().equals(Account.TypeEnum.DEPOSIT_ACCOUNT);
        return depositAccount;
    }

    @Test
    public void CreateDepositAccountApiTest() throws ApiException {
        CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
    }

    DepositAccount CreateAndCloseDepositAccount() throws ApiException {
        DepositAccount depositAccount = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        CloseAccountRequest closeAccountRequest = new CloseAccountRequest();
        CloseAccountRequestData closeAccountRequestData = new CloseAccountRequestData();
        CloseAccountRequestDataAttributes closeAccountRequestAttributes = new CloseAccountRequestDataAttributes();
        closeAccountRequestAttributes.setReason(CloseAccountRequestDataAttributes.ReasonEnum.BY_CUSTOMER);
        closeAccountRequestData.setAttributes(closeAccountRequestAttributes);
        closeAccountRequestData.setType(CloseAccountRequestData.TypeEnum.DEPOSIT_ACCOUNT_CLOSE);
        closeAccountRequest.setData(closeAccountRequestData);

        UnitAccountResponse res = unitApi.closeAccount(depositAccount.getId(), closeAccountRequest);
        assert res.getData().getType().equals(Account.TypeEnum.DEPOSIT_ACCOUNT);

        return (DepositAccount) res.getData();
    }

    @Test
    public void CloseDepositAccountApiTest() throws ApiException {
        CreateAndCloseDepositAccount();
    }

    @Test
    public void ReopenDepositAccountApiTest() throws ApiException {
        DepositAccount closedDepositAccount = CreateAndCloseDepositAccount();
        UnitAccountResponse res = unitApi.reopenAccount(closedDepositAccount.getId());
        assert res.getData().getType().equals(Account.TypeEnum.DEPOSIT_ACCOUNT);
    }

    private DepositAccount CreateAndFreezeDepositAccount() throws ApiException {
        DepositAccount depositAccount = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        FreezeAccountRequest freezeAccountRequest = new FreezeAccountRequest();
        FreezeAccountRequestData freezeAccountRequestData = new FreezeAccountRequestData();
        FreezeAccountRequestDataAttributes freezeAccountRequestDataAttributes = new FreezeAccountRequestDataAttributes();
        freezeAccountRequestDataAttributes.setReason(FreezeAccountRequestDataAttributes.ReasonEnum.FRAUD);
        freezeAccountRequestData.setType(FreezeAccountRequestData.TypeEnum.ACCOUNT_FREEZE);
        freezeAccountRequestData.setAttributes(freezeAccountRequestDataAttributes);
        freezeAccountRequest.setData(freezeAccountRequestData);
        UnitAccountResponse res = unitApi.freezeAccount(depositAccount.getId(), freezeAccountRequest);
        DepositAccount frozenDepositAccount = (DepositAccount) res.getData();
        assert frozenDepositAccount.getType().equals(Account.TypeEnum.DEPOSIT_ACCOUNT);
        return frozenDepositAccount;
    }

    @Test
    public void FreezeDepositAccountApiTest() throws ApiException {
        CreateAndFreezeDepositAccount();
    }

    @Test
    public void UnfreezeDepositAccountApiTest() throws ApiException {
        DepositAccount frozenAccount = CreateAndFreezeDepositAccount();
        UnitAccountResponse res = unitApi.unfreezeAccount(frozenAccount.getId());
        assert res.getData().getType().equals(Account.TypeEnum.DEPOSIT_ACCOUNT);
    }

    @Test
    public void GetDepositAccountLimitsApiTest() throws ApiException {
        DepositAccount depositAccount = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        UnitGetAccountLimitsResponse res = unitApi.getAccountLimits(depositAccount.getId());
        assert res.getData().getType().equals(Limits.TypeEnum.LIMITS);
    }
    
    @Test
    public void GetDepositAccountAvailableProducts() throws ApiException {
        DepositAccount depositAccount = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        UnitDepositProductsResponse res = unitApi.getDepositProductsList(depositAccount.getId());
        res.getData().forEach(product -> {
            assert product.getType().equals(DepositProduct.TypeEnum.ACCOUNT_DEPOSIT_PRODUCT);
        });
    }

    @Test
    public void GetDepositAccountBalanceHistory() throws ApiException {
        DepositAccount depositAccount = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        ListPageParameters pageParams = new ListPageParameters();
        pageParams.setLimit(20);
        GetAccountBalanceHistoryFilterParameter mainFilters = new GetAccountBalanceHistoryFilterParameter();
        mainFilters.setAccountId(depositAccount.getId());
        UnitAccountBalanceHistoryResponse res = unitApi.getAccountBalanceHistory(pageParams, mainFilters);
        res.getData().forEach(accountEndOfDay -> {
            assert accountEndOfDay.getType().equals(AccountEndOfDay.TypeEnum.ACCOUNT_END_OF_DAY);
        });
    }

    DepositAccount CreateDepositAccountAndAddOwnersToIt (List<DepositAccountOwner> owners) throws ApiException {
        DepositAccount depositAccount = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        AddAccountOwnersRequest addAccountOwnersRequest = new AddAccountOwnersRequest();
        addAccountOwnersRequest.setData(owners);
        UnitDepositAccountResponse res = unitApi.addAccountOwners(depositAccount.getId(), addAccountOwnersRequest);
        assert res.getData().getType().equals(Account.TypeEnum.DEPOSIT_ACCOUNT);
        return res.getData();
    }

    @Test   
    public void AddDepositAccountOwnersApiTest() throws ApiException {
        List<DepositAccountOwner> owners = new ArrayList<DepositAccountOwner>();
        DepositAccount depositAccountOne = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        DepositAccount depositAccountTwo = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        DepositAccountOwner ownerOne = new DepositAccountOwner();
        ownerOne.setId(depositAccountOne.getId());
        ownerOne.setType(DepositAccountOwner.TypeEnum.CUSTOMER);
        DepositAccountOwner ownerTwo = new DepositAccountOwner();
        ownerTwo.setId(depositAccountTwo.getId());
        ownerTwo.setType(DepositAccountOwner.TypeEnum.CUSTOMER);
        owners.add(ownerOne);
        owners.add(ownerTwo);
        CreateDepositAccountAndAddOwnersToIt(owners);
    }

    @Test
    public void RemoveDepositAccountOwnersApiTest() throws ApiException {
        List<DepositAccountOwner> owners = new ArrayList<DepositAccountOwner>();
        DepositAccount depositAccountOne = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        DepositAccount depositAccountTwo = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        DepositAccountOwner ownerOne = new DepositAccountOwner();
        ownerOne.setId(depositAccountOne.getId());
        ownerOne.setType(DepositAccountOwner.TypeEnum.CUSTOMER);
        DepositAccountOwner ownerTwo = new DepositAccountOwner();
        ownerTwo.setId(depositAccountTwo.getId());
        ownerTwo.setType(DepositAccountOwner.TypeEnum.CUSTOMER);
        owners.add(ownerOne);
        owners.add(ownerTwo);
        DepositAccount accountWithOwners = CreateDepositAccountAndAddOwnersToIt(owners);

        RemoveAccountOwnersRequest removeAccountOwnersRequest = new RemoveAccountOwnersRequest();
        removeAccountOwnersRequest.setData(owners);
        UnitDepositAccountResponse res = unitApi.removeAccountOwners(accountWithOwners.getId(), removeAccountOwnersRequest);
        assert res.getData().getType().equals(Account.TypeEnum.DEPOSIT_ACCOUNT);
    }

    public static CreditAccount CreateCreditAccount(UnitApi unitApi) throws ApiException {
        BusinessCustomer customer = CreateBusinessCustomer(unitApi);

        CreateCreditAccount cca = new CreateCreditAccount();
        CreateCreditAccountAttributes attributes = new CreateCreditAccountAttributes();
        attributes.setCreditTerms("credit_terms_test");
        attributes.setCreditLimit(20000);

        CreateCreditAccountRelationships relationships = new CreateCreditAccountRelationships();
        CustomerRelationshipData customerRelationshipData = new CustomerRelationshipData();
        customerRelationshipData.setId(customer.getId());
        customerRelationshipData.setType(CustomerRelationshipData.TypeEnum.CUSTOMER);
        CustomerRelationship customerLinkageRelationship = new CustomerRelationship();
        customerLinkageRelationship.setData(customerRelationshipData);

        relationships.setCustomer(customerLinkageRelationship);

        cca.setAttributes(attributes);
        cca.setRelationships(relationships);

        CreateAccountRequest ca = new CreateAccountRequest();
        ca.setData(new CreateAccountRequestData(cca));

        CreditAccount creditAccount = (CreditAccount) unitApi.createAccount(ca).getData();
        assert creditAccount.getType().equals(Account.TypeEnum.CREDIT_ACCOUNT);
        return creditAccount;
    }

    @Test
    public void CreateCreditAccountApiTest() throws ApiException {
        CreateCreditAccount(unitApi);
    }

    @Test
    public void CloseCreditAccountApiTest() throws ApiException {
        CreditAccount creditAccount = CreateCreditAccount(unitApi);
        CloseAccountRequest closeAccountRequest = new CloseAccountRequest();
        CloseAccountRequestData closeAccountRequestData = new CloseAccountRequestData();
        CloseAccountRequestDataAttributes closeAccountRequestAttributes = new CloseAccountRequestDataAttributes();
        closeAccountRequestAttributes.setReason(CloseAccountRequestDataAttributes.ReasonEnum.BY_CUSTOMER);
        closeAccountRequestData.setAttributes(closeAccountRequestAttributes);
        closeAccountRequestData.setType(CloseAccountRequestData.TypeEnum.CREDIT_ACCOUNT_CLOSE);
        closeAccountRequest.setData(closeAccountRequestData);

        UnitAccountResponse res = unitApi.closeAccount(creditAccount.getId(), closeAccountRequest);
        assert res.getData().getType().equals(Account.TypeEnum.CREDIT_ACCOUNT);
    }

    public CreditAccount CreateAndFreezeCreditAccount() throws ApiException {
        CreditAccount creditAccount = CreateCreditAccount(unitApi);
        FreezeAccountRequest freezeAccountRequest = new FreezeAccountRequest();
        FreezeAccountRequestData freezeAccountRequestData = new FreezeAccountRequestData();
        FreezeAccountRequestDataAttributes freezeAccountRequestDataAttributes = new FreezeAccountRequestDataAttributes();
        freezeAccountRequestDataAttributes.setReason(FreezeAccountRequestDataAttributes.ReasonEnum.FRAUD);
        freezeAccountRequestData.setType(FreezeAccountRequestData.TypeEnum.CREDIT_ACCOUNT_FREEZE);
        freezeAccountRequestData.setAttributes(freezeAccountRequestDataAttributes);
        freezeAccountRequest.setData(freezeAccountRequestData);
        UnitAccountResponse res = unitApi.freezeAccount(creditAccount.getId(), freezeAccountRequest);
        CreditAccount frozenCreditAccount = (CreditAccount) res.getData();
        assert frozenCreditAccount.getType().equals(Account.TypeEnum.CREDIT_ACCOUNT);
        return frozenCreditAccount;
    }

    @Test
    public void FreezeCreditAccountApiTest() throws ApiException {
        CreateAndFreezeCreditAccount();
    }

    @Test
    public void UnfreezeCreditAccounApiTest() throws ApiException {
        CreditAccount frozenAccount = CreateAndFreezeCreditAccount();
        UnitAccountResponse res = unitApi.unfreezeAccount(frozenAccount.getId());
        assert res.getData().getType().equals(Account.TypeEnum.CREDIT_ACCOUNT);
    }

    @Test 
    public void GetCreditAccountLimitsApiTest() throws ApiException {
        CreditAccount creditAccount = CreateCreditAccount(unitApi);
        UnitGetAccountLimitsResponse res = unitApi.getAccountLimits(creditAccount.getId());
        assert res.getData().getType().equals(Limits.TypeEnum.CREDIT_LIMITS);
    }

    @Test
    public void GetCreditAccountBalanceHistoryApiTest() throws ApiException {
        CreditAccount depositAccount = CreateCreditAccount(unitApi);
        ListPageParameters pageParams = new ListPageParameters();
        pageParams.setLimit(20);
        GetAccountBalanceHistoryFilterParameter mainFilters = new GetAccountBalanceHistoryFilterParameter();
        mainFilters.setAccountId(depositAccount.getId());
        UnitAccountBalanceHistoryResponse res = unitApi.getAccountBalanceHistory(pageParams, mainFilters);
        res.getData().forEach(accountEndOfDay -> {
            assert accountEndOfDay.getType().equals(AccountEndOfDay.TypeEnum. ACCOUNT_END_OF_DAY);
        });
    }

    @Test 
    public void GetCreditAccountRepaymentInformation() throws ApiException {
        CreditAccount creditAccount = CreateCreditAccount(unitApi);
        UnitRepaymentInformationResponse res = unitApi.getRepaymentInformation(creditAccount.getId());
        assert res.getData().getType().equals(RepaymentInformation.TypeEnum.CREDIT_ACCOUNT_REPAYMENT_INFORMATION);
    }

    WalletAccount CreateWalletAccount() throws ApiException {
        BusinessCustomer customer = CreateBusinessCustomer(unitApi);

        CreateWalletAccount req = new CreateWalletAccount();
        CreateWalletAccountAttributes attributes = new CreateWalletAccountAttributes();
        attributes.setWalletTerms("walletDefault");

        CreateWalletAccountRelationships relationships = new CreateWalletAccountRelationships();
        CustomerRelationshipData customerRelationshipData = new CustomerRelationshipData();
        customerRelationshipData.setId(customer.getId());
        customerRelationshipData.setType(CustomerRelationshipData.TypeEnum.CUSTOMER);
        CustomerRelationship customerLinkageRelationship = new CustomerRelationship();
        customerLinkageRelationship.setData(customerRelationshipData);

        relationships.setCustomer(customerLinkageRelationship);

        req.setAttributes(attributes);
        req.setRelationships(relationships);

        CreateAccountRequest ca = new CreateAccountRequest();
        ca.setData(new CreateAccountRequestData(req));

        WalletAccount walletAccount = (WalletAccount) unitApi.createAccount(ca).getData();
        assert walletAccount.getType().equals(Account.TypeEnum.WALLET_ACCOUNT);
        return walletAccount;
    }

    @Test
    public void CreateWalletAccountApiTest() throws ApiException {
        CreateWalletAccount();
    }

    WalletAccount CreateAndCloseWalletAccount() throws ApiException {
        WalletAccount walletAccount = CreateWalletAccount();
        CloseAccountRequest closeAccountRequest = new CloseAccountRequest();
        CloseAccountRequestData closeAccountRequestData = new CloseAccountRequestData();
        CloseAccountRequestDataAttributes closeAccountRequestAttributes = new CloseAccountRequestDataAttributes();
        closeAccountRequestAttributes.setReason(CloseAccountRequestDataAttributes.ReasonEnum.BY_CUSTOMER);
        closeAccountRequestData.setAttributes(closeAccountRequestAttributes);
        closeAccountRequestData.setType(CloseAccountRequestData.TypeEnum.DEPOSIT_ACCOUNT_CLOSE);
        closeAccountRequest.setData(closeAccountRequestData);

        UnitAccountResponse res = unitApi.closeAccount(walletAccount.getId(), closeAccountRequest);
        assert res.getData().getType().equals(Account.TypeEnum.WALLET_ACCOUNT);

        return (WalletAccount) res.getData();
    }

    @Test 
    public void CloseWalletAccountApiTest() throws ApiException {
        CreateAndCloseWalletAccount();
    }

    @Test
    public void ReopenWalletAccountApiTest() throws ApiException {
        WalletAccount closedWalletAccount = CreateAndCloseWalletAccount();
        UnitAccountResponse res = unitApi.reopenAccount(closedWalletAccount.getId());
        assert res.getData().getType().equals(Account.TypeEnum.WALLET_ACCOUNT);
    }

    WalletAccount CreateAndFreezeWalletAccount() throws ApiException {
        DepositAccount walletAccount = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        FreezeAccountRequest freezeAccountRequest = new FreezeAccountRequest();
        FreezeAccountRequestData freezeAccountRequestData = new FreezeAccountRequestData();
        FreezeAccountRequestDataAttributes freezeAccountRequestDataAttributes = new FreezeAccountRequestDataAttributes();
        freezeAccountRequestDataAttributes.setReason(FreezeAccountRequestDataAttributes.ReasonEnum.FRAUD);
        freezeAccountRequestData.setType(FreezeAccountRequestData.TypeEnum.ACCOUNT_FREEZE);
        freezeAccountRequestData.setAttributes(freezeAccountRequestDataAttributes);
        freezeAccountRequest.setData(freezeAccountRequestData);
        UnitAccountResponse res = unitApi.freezeAccount(walletAccount.getId(), freezeAccountRequest);
        WalletAccount frozenWalletAccount = (WalletAccount) res.getData();
        assert frozenWalletAccount.getType().equals(Account.TypeEnum.WALLET_ACCOUNT);
        return frozenWalletAccount;
    }

    @Test
    public void FreezeWalletAccountApiTest() throws ApiException {
        CreateAndFreezeWalletAccount();
    }

    @Test
    public void UnfreezeWalletAccountApiTest() throws ApiException {
        WalletAccount frozenAccount = CreateAndFreezeWalletAccount();
        UnitAccountResponse res = unitApi.unfreezeAccount(frozenAccount.getId());
        assert res.getData().getType().equals(Account.TypeEnum.WALLET_ACCOUNT);
    }

    @Test
    public void GetWalletAccountLimitsApiTest() throws ApiException {
        DepositAccount depositAccount = CreateDepositAccount(unitApi, CreateIndividualCustomer(unitApi));
        UnitGetAccountLimitsResponse res = unitApi.getAccountLimits(depositAccount.getId());
        assert res.getData().getType().equals(Limits.TypeEnum.WALLET_LIMITS);
    }

    @Test
    public void GetWalletAccountBalanceHistoryApiTest() throws ApiException {
        WalletAccount walletAccount = CreateWalletAccount();
        ListPageParameters pageParams = new ListPageParameters();
        pageParams.setLimit(20);
        GetAccountBalanceHistoryFilterParameter mainFilters = new GetAccountBalanceHistoryFilterParameter();
        mainFilters.setAccountId(walletAccount.getId());
        UnitAccountBalanceHistoryResponse res = unitApi.getAccountBalanceHistory(pageParams, mainFilters);
        res.getData().forEach(accountEndOfDay -> {
            assert accountEndOfDay.getType().equals(AccountEndOfDay.TypeEnum.ACCOUNT_END_OF_DAY);
        });
    }
}
