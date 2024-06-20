require 'spec_helper'
require 'json'
require 'date'

# Unit tests for OpenapiClient::Counterparty
RSpec.describe 'Counterparty' do
  before do
    configure_tests
  end

  describe 'test an instance of Counterparty' do
    let(:api_instance) { OpenapiClient::CreateCounterpartyApi.new(OpenapiClient::ApiClient.new(configuration)) }

    it 'should create an instance of Counterparty' do
      request = { data: OpenapiClient::CreatePlaidCounterparty.new(type: 'achCounterparty',
                                                                   attributes:
                                                                     OpenapiClient::CreatePlaidCounterpartyAttributes.new(
                                                                       {type: "Business",
                                                                        name: 'test',
                                                                        tags: { "purpose": 'tax' },
                                                                        idempotency_key: '1234567890',
                                                                        plaid_processor_token: 'processor-sandbox-7179b936-476c-4b3c-a2f8-8255d8cd5b5b'}),
                                                                   relationships: OpenapiClient::CreateCounterpartyRelationships.new(
                                                                     customer: { "data": { "type": 'customer',
                                                                                           "id": '823139' } }
                                                                   ).to_hash).to_hash }
      response = api_instance.execute(request)
      expect(response.data.type).to eq('achCounterparty')
    end
  end
end