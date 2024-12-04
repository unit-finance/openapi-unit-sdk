# frozen_string_literal: true

require 'unit_openapi_ruby_sdk'
require_relative '../spec_helper'

RSpec.describe 'Card' do
  before do
    configure_tests
  end

  describe 'test an instance of CreateCard' do
    let(:api_instance) { UnitOpenapiRubySdk::UnitApi.new(UnitOpenapiRubySdk::ApiClient.new(configuration)) }
    let(:account_for_business) { api_instance.create_account(
      data: {
        type: 'depositAccount',
        attributes: UnitOpenapiRubySdk::CreateDepositAccountAttributes.new(
          deposit_product: 'checking',
          tags: { "purpose": 'checking' },
          idempotency_key: '1234567890'
        ).to_hash, relationships: RELATIONSHIPS_BUSINESS }).data.id}


    it 'should create an individual debit card' do
      request = { data: UnitOpenapiRubySdk::CreateIndividualDebitCardRequest.new(type: 'individualDebitCard', attributes:
        UnitOpenapiRubySdk::CreateIndividualDebitCardRequestAttributes.new(
          { shipping_address: ADDRESS, tags: { "purpose": 'checking' },
            additional_embossed_text: 'additional_text', expiry_date: '03/28' }
        ),
                                                                            relationships: UnitOpenapiRubySdk::CardRelationships.new(account: { "data": { "type": 'depositAccount',
                                                                                                                                                     "id": '4009403' } })).to_hash }
      response = api_instance.create_card(request)
      expect(response.data.type).to eq('individualDebitCard')
    end

    it 'should create a business debit card' do
      request = { data: UnitOpenapiRubySdk::CreateBusinessDebitCardRequest.new(type: 'businessDebitCard', attributes:
        UnitOpenapiRubySdk::CreateBusinessCardAttributes.new(
          { shipping_address: ADDRESS, address: ADDRESS, full_name: FULL_NAME, phone: PHONE,
            email: EMAIL, date_of_birth: DATE_OF_BIRTH, nationality: 'US', ssn: SSN,
            tags: { "purpose": 'business' }, limits: LIMITS, idempotency_key: '1234567890',
            print_only_business_name: false, expiry_date: '03/28' }
        ),
                                                                          relationships: UnitOpenapiRubySdk::CardRelationships.new(account: { "data": { "type": 'account',
                                                                                                                                                   "id": '3969030' } })).to_hash }
      response = api_instance.create_card(request)
      expect(response.data.type).to eq('businessDebitCard')
    end
  end
end