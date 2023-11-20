# frozen_string_literal: true

require 'openapi_client'
require_relative '../spec_helper'

RSpec.describe 'Card' do
  before do
    configure_tests
  end

  describe 'test an instance of CreateCard' do
    let(:api_instance) { OpenapiClient::CreateACardApi.new(OpenapiClient::ApiClient.new(configuration)) }

    it 'should create an individual debit card' do
      request = { data: OpenapiClient::CreateIndividualDebitCard.new(type: 'individualDebitCard', attributes:
        OpenapiClient::CreateIndividualDebitCardAttributes.new(
          { shipping_address: ADDRESS, tags: { "purpose": 'checking' },
            additional_embossed_text: 'additional_text', expiry_date: '03/27' }
        ),
                                                                     relationships: OpenapiClient::Relationships.new(account: { "data": { "type": 'depositAccount',
                                                                                                                                          "id": '2002413' } })).to_hash }
      response = api_instance.execute(request)
      expect(response.data.type).to eq('individualDebitCard')
    end

    it 'should create a business debit card' do
      request = { data: OpenapiClient::CreateBusinessDebitCard.new(type: 'businessDebitCard', attributes:
        OpenapiClient::CreateBusinessDebitCardAttributes.new(
          { shipping_address: ADDRESS, address: ADDRESS, full_name: FULL_NAME, phone: PHONE,
            email: EMAIL, date_of_birth: DATE_OF_BIRTH, nationality: 'US', ssn: SSN,
            tags: { "purpose": 'business' }, limits: LIMITS, idempotency_key: '1234567890',
            print_only_business_name: false, expiry_date: '03/27' }
        ),
                                                                   relationships: OpenapiClient::Relationships.new(account: { "data": { "type": 'account',
                                                                                                                                        "id": '1567861' } })).to_hash }
      response = api_instance.execute(request)
      expect(response.data.type).to eq('businessDebitCard')
    end
  end
end