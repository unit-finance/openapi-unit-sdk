require 'spec_helper'

RSpec.describe 'Account' do
  before do
    configure_tests
  end

  describe 'test an instance of Account' do

    let(:api_instance) { OpenapiClient::UnitApi.new(OpenapiClient::ApiClient.new(configuration)) }

    it 'Should create deposit account' do
      request = {
        data: {
          type: 'depositAccount',
          attributes: OpenapiClient::CreateDepositAccountAttributes.new(
            deposit_product: 'checking',
            tags: { "purpose": 'checking' },
            idempotency_key: '1234567890'
          ).to_hash,
          relationships: OpenapiClient::CreateDepositAccountRelationships.new(
            customer: { "data": { "type": 'customer', "id": '751009' } }
          ).to_hash
        }}
      response = api_instance.create_account(request)
      expect(response.data.type).to eq('depositAccount')
    end



    it 'Should create a credit account' do
      request = { data: OpenapiClient::CreateCreditAccount.new(type: 'creditAccount',
                                                               attributes:
                                                                 OpenapiClient::CreateCreditAccountAttributes.new(
                                                                   { credit_terms: 'credit_terms_test',
                                                                     credit_limit: 20_000,
                                                                     tags: { "purpose": 'tax' } }
                                                                 ),
                                                               relationships: OpenapiClient::CreateDepositAccountRelationships.new(
                                                                 customer: { "data": { "type": 'customer',
                                                                                       "id": '851228' } }
                                                               ).to_hash).to_hash }
      response = api_instance.create_account(request)
      expect(response.data.type).to eq('creditAccount')
    end
  end
end