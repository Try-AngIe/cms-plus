package kr.or.kosa.cmsplusmain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import net.datafaker.Faker;
import net.datafaker.providers.base.Options;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.Transactional;
import kr.or.kosa.cmsplusmain.domain.billing.entity.Billing;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingProduct;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingStatus;
import kr.or.kosa.cmsplusmain.domain.billing.entity.BillingType;
import kr.or.kosa.cmsplusmain.domain.billing.repository.BillingRepository;
import kr.or.kosa.cmsplusmain.domain.contract.entity.Contract;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractProduct;
import kr.or.kosa.cmsplusmain.domain.contract.entity.ContractStatus;
import kr.or.kosa.cmsplusmain.domain.contract.repository.ContractRepository;
import kr.or.kosa.cmsplusmain.domain.kafka.MessageSendMethod;
import kr.or.kosa.cmsplusmain.domain.member.entity.Address;
import kr.or.kosa.cmsplusmain.domain.member.entity.Member;
import kr.or.kosa.cmsplusmain.domain.member.repository.MemberRepository;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Bank;
import kr.or.kosa.cmsplusmain.domain.payment.entity.Payment;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.CardPaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.CmsPaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.PaymentMethod;
import kr.or.kosa.cmsplusmain.domain.payment.entity.method.PaymentMethodInfo;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.AutoPaymentType;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.BuyerPaymentType;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentType;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.PaymentTypeInfo;
import kr.or.kosa.cmsplusmain.domain.payment.entity.type.VirtualAccountPaymentType;
import kr.or.kosa.cmsplusmain.domain.product.entity.Product;
import kr.or.kosa.cmsplusmain.domain.product.entity.ProductStatus;
import kr.or.kosa.cmsplusmain.domain.product.repository.ProductRepository;
import kr.or.kosa.cmsplusmain.domain.settings.entity.SimpConsentSetting;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.UserRole;
import kr.or.kosa.cmsplusmain.domain.vendor.entity.Vendor;
import kr.or.kosa.cmsplusmain.domain.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SampleDataLoader {

	private final VendorRepository vendorRepository;
	private final ProductRepository productRepository;
	private final MemberRepository memberRepository;
	private final ContractRepository contractRepository;
	private final BillingRepository billingRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	private final Random random = new Random(System.currentTimeMillis());
	private final RandomGenerator randomGenerator = new RandomGenerator(random);
	private final Faker faker = new Faker(new Locale("ko", "ko"));
	private final Options options = faker.options();

	// 학원 관련 상수 정의
	private static final String[] SUBJECTS = {"수학", "영어", "과학", "국어", "사회", "코딩", "미술", "음악", "체육"};
	private static final String[] LEVELS = {"초급", "중급", "고급", "심화", "기초"};
	private static final String[] TYPES = {"특강", "정규반", "단과", "집중", "1:1", "그룹", "온라인"};
	private static final String[] DURATIONS = {"3개월", "6개월", "1년", "단기", "장기"};

	@Transactional
	public void init() {
		// 기본 학원(고객) 생성
		Vendor academy = vendorRepository.save(
			createAcademyWithDefaultProduct(
				"hyosung",
				"김효성",
				"gusehd502@naver.com",
				"01026963279",
				"Qwer123!")
		);

		// 샘플 데이터 생성
		generateSampleData(academy, 50, 1000, 2000, 1000);
	}

	/**
	 * 샘플 데이터 생성 메서드
	 * @param academy 학원(고객) 엔티티
	 * @param productCnt 생성할 상품 수
	 * @param memberCnt 생성할 회원 수
	 * @param contractCnt 생성할 계약 수
	 * @param billingCnt 생성할 청구 수
	 */
	public void generateSampleData(Vendor academy, int productCnt, int memberCnt, int contractCnt, int billingCnt) {
		List<Product> products = productRepository.saveAll(createProductsForAcademy(academy, productCnt));
		List<Member> members = memberRepository.saveAll(createMembersForAcademy(academy, memberCnt));
		List<Contract> contracts = contractRepository.saveAll(createContractsForMembers(members, products, contractCnt));
		List<Billing> billings = billingRepository.saveAll(createBillingsForContracts(contracts, products, billingCnt));
		members.forEach(Member::calcContractPriceAndCnt);
		memberRepository.saveAll(members);
	}

	/**
	 * 학원에 대한 상품 생성 메서드
	 * @param academy 학원(고객) 엔티티
	 * @param productCnt 생성할 상품 수
	 * @return 생성된 상품 리스트
	 */
	public List<Product> createProductsForAcademy(Vendor academy, int productCnt) {
		List<Product> products = new ArrayList<>(productCnt);
		for (int i = 0; i < productCnt; i++) {
			Product product = createTestProduct(academy, i);
			products.add(product);
		}
		return products;
	}

	/**
	 * 계약에 대한 청구 생성 메서드
	 * @param contracts 계약 리스트
	 * @param products 상품 리스트
	 * @param count 생성할 청구 수
	 * @return 생성된 청구 리스트
	 */
	public List<Billing> createBillingsForContracts(List<Contract> contracts, List<Product> products, int count) {
		List<Billing> billings = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			Contract contract = randomGenerator.getRandomInList(contracts);

			// 월을 먼저 선택하고, 그 다음 날짜를 선택
			int month = random.nextInt(9) + 1;  // 1 ~ 9월 중 선택
			int day = random.nextInt(17) + 1;

			LocalDate billingDate = LocalDate.of(2024, month, day);

			List<BillingProduct> billingProducts = createBillingProducts(products);

			Billing billing = new Billing(contract, options.option(BillingType.class), billingDate, billingDate.getDayOfMonth(), billingProducts);
			billing.setBillingStatus(getRandomBillingStatus(billingDate));
			billing.setInvoiceMessage(generateRandomInvoiceMessage());

			// 청구 상태에 따른 결제 시각 및 청구서 발송 시각 설정
			switch (billing.getBillingStatus()) {
				case PAID -> {
					billing.setPaidDateTime(billingDate.atTime(random.nextInt(23), random.nextInt(59)));
					if (random.nextBoolean()) {
						billing.setInvoiceSendDateTime(billing.getPaidDateTime().minusHours(random.nextInt(24)));
					}
				}
				case WAITING_PAYMENT, NON_PAID -> billing.setInvoiceSendDateTime(billingDate.atTime(random.nextInt(23), random.nextInt(59)));
			}

			billings.add(billing);
		}
		return billings;
	}

	/**
	 * 청구 상품 생성 메서드
	 * @param products 상품 리스트
	 * @return 생성된 청구 상품 리스트
	 */
	public List<BillingProduct> createBillingProducts(List<Product> products) {
		List<BillingProduct> billingProducts = new ArrayList<>();
		int productCount = random.nextInt(9) + 1; // 1~9개의 상품 생성
		for (int i = 0; i < productCount; i++) {
			Product product = options.nextElement(products);
			BillingProduct billingProduct = BillingProduct.builder()
				.product(product)
				.name(product.getName())
				.price(product.getPrice())
				.quantity(random.nextInt(9) + 1)
				.build();
			billingProducts.add(billingProduct);
		}
		return billingProducts;
	}

	/**
	 * 학원에 대한 회원 생성 메서드
	 * @param academy 학원(고객) 엔티티
	 * @param count 생성할 회원 수
	 * @return 생성된 회원 리스트
	 */
	public List<Member> createMembersForAcademy(Vendor academy, int count) {
		List<Member> createdMembers = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			Member member = createMember(academy, i);
			member = memberRepository.save(member);
			createdMembers.add(member);
		}

		return createdMembers;
	}

	/**
	 * 회원 생성 메서드
	 * @param academy 학원(고객) 엔티티
	 * @param index 회원 인덱스
	 * @return 생성된 회원 엔티티
	 */
	public Member createMember(Vendor academy, int index) {
		String name = faker.name().lastName() + faker.name().firstName();
		String email = "%s@example.com".formatted(name+index);
		String phone = randomGenerator.generateRandomPhone();
		String homePhone = randomGenerator.generateRandomHomePhone();
		Address address = new Address(faker.address().zipCode(), faker.address().streetAddress(), faker.address().secondaryAddress());
		LocalDate enrollDate = LocalDate.from(faker.timeAndDate().past().atZone(ZoneId.of("Asia/Seoul")));

		return Member.builder()
			.vendor(academy)
			.name(name)
			.email(email)
			.phone(phone)
			.homePhone(homePhone)
			.address(address)
			.memo(faker.educator().course())
			.enrollDate(enrollDate)
			.invoiceSendMethod(options.option(MessageSendMethod.class))
			.autoInvoiceSend(random.nextBoolean())
			.autoBilling(random.nextBoolean())
			.build();
	}

	/**
	 * 기본 상품이 포함된 학원 생성 메서드
	 * @param username 사용자명
	 * @param name 이름
	 * @param email 이메일
	 * @param phone 전화번호
	 * @param password 비밀번호
	 * @return 생성된 학원(고객) 엔티티
	 */
	public Vendor createAcademyWithDefaultProduct(String username, String name, String email, String phone, String password) {
		// 간편동의 설정
		SimpConsentSetting simpConsentSetting = new SimpConsentSetting();
		simpConsentSetting.addPaymentMethod(PaymentMethod.CMS);
		simpConsentSetting.addPaymentMethod(PaymentMethod.CARD);

		// 학원(고객)
		Vendor academy = createTestAcademy(username, name, email, phone, password);
		academy.setSimpConsentSetting(simpConsentSetting);
		academy = vendorRepository.save(academy);

		// 기본 상품
		Product defaultProduct = createTestProduct(academy, -1);
		defaultProduct = productRepository.save(defaultProduct);

		// 간편동의 기본상품 추가
		academy.getSimpConsentSetting().addProduct(defaultProduct);

		return vendorRepository.save(academy);
	}

	/**
	 * 회원에 대한 계약 생성 메서드
	 * @param members 회원 리스트
	 * @param products 상품 리스트
	 * @param count 생성할 계약 수
	 * @return 생성된 계약 리스트
	 */
	public List<Contract> createContractsForMembers(List<Member> members, List<Product> products, int count) {
		List<Contract> createdContracts = new ArrayList<>();

		for (int i = 0; i < count; i++) {
			Member member = options.nextElement(members);
			Contract contract = createContract(member, i, products);
			member.getContracts().add(contract);
			createdContracts.add(contract);}

		return createdContracts;
	}

	/**
	 * 계약 생성 메서드
	 * @param member 회원 엔티티
	 * @param index 계약 인덱스
	 * @param products 상품 리스트
	 * @return 생성된 계약 엔티티
	 */
	public Contract createContract(Member member, int index, List<Product> products) {
		Payment payment = generatePayment();
		LocalDate startDate = LocalDate.from(faker.timeAndDate().past(150, TimeUnit.DAYS).atZone(ZoneId.of("Asia/Seoul")));
		LocalDate endDate = startDate.plusMonths(random.nextInt(12) + 1);

		ContractStatus status = (LocalDate.now().isAfter(endDate)) ? ContractStatus.DISABLED : ContractStatus.ENABLED;

		Contract contract = Contract.builder()
			.vendor(member.getVendor())
			.member(member)
			.contractName("수강계약(%s)".formatted(startDate.format(DateTimeFormatter.ofPattern("MM.dd"))))
			.contractDay(random.nextInt(28) + 1)
			.payment(payment)
			.contractStatus(status)
			.contractStartDate(startDate)
			.contractEndDate(endDate)
			.build();

		addRandomContractProducts(contract, products);

		return contract;
	}

	/**
	 * 계약에 랜덤 상품 추가 메서드
	 * @param contract 계약 엔티티
	 * @param products 상품 리스트
	 */
	public void addRandomContractProducts(Contract contract, List<Product> products) {
		int productCount = random.nextInt(9) + 1; // 1에서 9개의 상품 추가

		for (int i = 0; i < productCount && i < products.size(); i++) {
			Product product = products.get(i);
			ContractProduct contractProduct = ContractProduct.builder()
				.contract(contract)
				.product(product)
				.name(product.getName())
				.price(product.getPrice())
				.quantity(random.nextInt(9) + 1) // 1에서 9개의 수량
				.build();

			contract.addContractProduct(contractProduct);
		}
	}

	/**
	 * 결제 정보 생성 메서드
	 * @return 생성된 결제 엔티티
	 */
	public Payment generatePayment() {
		PaymentType paymentType = options.option(PaymentType.class);
		PaymentMethod paymentMethod = getRandomPaymentMethod(paymentType);

		return Payment.builder()
			.paymentType(paymentType)
			.paymentTypeInfo(generatePaymentTypeInfo(paymentType))
			.paymentMethod(paymentMethod)
			.paymentMethodInfo(generatePaymentMethodInfo(paymentMethod))
			.build();
	}

	/**
	 * 랜덤 결제 방식 선택 메서드
	 * @param paymentType 결제 유형
	 * @return 선택된 결제 방식
	 */
	public PaymentMethod getRandomPaymentMethod(PaymentType paymentType) {
		if (paymentType == PaymentType.AUTO) {
			return options.option(PaymentMethod.CARD, PaymentMethod.CMS);
		} else {
			return null;
		}
	}

	/**
	 * 결제 유형 정보 생성 메서드
	 * @param paymentType 결제 유형
	 * @return 생성된 결제 유형 정보
	 */
	public PaymentTypeInfo generatePaymentTypeInfo(PaymentType paymentType) {
		return switch (paymentType) {
			case AUTO -> AutoPaymentType.builder()
				.consentImgUrl(random.nextBoolean() ? ("blob:http://example.com/consent" + random.nextInt(1000) + ".jpg") : null)
				.simpleConsentReqDateTime(LocalDateTime.now().minusDays(random.nextInt(30)))
				.build();
			case BUYER -> BuyerPaymentType.builder()
				.availableMethods(Set.of(PaymentMethod.CARD, PaymentMethod.ACCOUNT))
				.build();
			case VIRTUAL -> VirtualAccountPaymentType.builder()
				.bank(options.option(Bank.class))
				.accountNumber(randomGenerator.generateRandomAccountNumber())
				.accountOwner(faker.name().lastName()+faker.name().firstName())
				.build();
		};
	}

	/**
	 * 결제 방식 정보 생성 메서드
	 * @param paymentMethod 결제 방식
	 * @return 생성된 결제 방식 정보
	 */
	public PaymentMethodInfo generatePaymentMethodInfo(PaymentMethod paymentMethod) {
		if (paymentMethod == null) {
			return null;
		}
		return switch (paymentMethod) {
			case CARD -> CardPaymentMethod.builder()
				.cardNumber(randomGenerator.generateRandomCardNumber())
				.cardMonth(random.nextInt(12) + 1)
				.cardYear(LocalDate.now().getYear() + random.nextInt(10))
				.cardOwner(faker.name().lastName()+faker.name().firstName())
				.cardOwnerBirth(faker.timeAndDate().birthday())
				.build();
			case CMS, ACCOUNT -> CmsPaymentMethod.builder()
				.bank(options.option(Bank.class))
				.accountNumber(randomGenerator.generateRandomAccountNumber())
				.accountOwner(faker.name().lastName()+faker.name().firstName())
				.accountOwnerBirth(faker.timeAndDate().birthday())
				.build();
		};
	}

	/**
	 * 테스트용 학원(고객) 생성 메서드
	 * @param username 사용자명
	 * @param name 이름
	 * @param email 이메일
	 * @param phone 전화번호
	 * @param password 비밀번호
	 * @return 생성된 학원(고객) 엔티티
	 */
	public Vendor createTestAcademy(String username, String name, String email, String phone, String password) {
		return Vendor.builder()
			.username(username)
			.password(passwordEncoder.encode(password))
			.name(name)
			.email(email)
			.phone(phone)
			.homePhone("021235678")
			.department("수학반")
			.role(UserRole.ROLE_VENDOR)
			.build();
	}

	/**
	 * 유니크한 상품명 생성 메서드
	 * @param count 생성할 상품명 수
	 * @return 생성된 상품명 리스트
	 */
	public static List<String> generateUniqueProductNames(int count) {
		Set<String> uniqueNames = new HashSet<>();
		Random random = new Random();

		while (uniqueNames.size() < count) {
			String subject = SUBJECTS[random.nextInt(SUBJECTS.length)];
			String level = LEVELS[random.nextInt(LEVELS.length)];
			String type = TYPES[random.nextInt(TYPES.length)];
			String duration = DURATIONS[random.nextInt(DURATIONS.length)];

			String productName = String.format("%s %s %s %s", subject, level, type, duration);
			uniqueNames.add(productName);
		}

		List<String> productList = new ArrayList<>(uniqueNames);
		Collections.shuffle(productList);
		return productList;
	}

	private final List<String> randomProductNames = generateUniqueProductNames(50);

	/**
	 * 테스트용 상품 생성 메서드
	 * @param vendor 학원(고객) 엔티티
	 * @param idx 상품 인덱스
	 * @return 생성된 상품 엔티티
	 */
	public Product createTestProduct(Vendor vendor, int idx) {
		return Product.builder()
			.vendor(vendor)
			.status(ProductStatus.ENABLED)
			.name(idx == -1 ? "기본상품" : randomProductNames.get(idx % randomProductNames.size()))
			.price(idx == -1 ? 0 : (int) Double.parseDouble(faker.commerce().price(10000, 1000000)))
			.memo(idx == -1 ? null : faker.book().title() + " 교재 사용")
			.build();
	}

	/**
	 * 랜덤 날짜 생성 메서드
	 * @param from 시작일
	 * @param to 종료일
	 * @return 생성된 랜덤 날짜
	 */
	public LocalDate generateRandomDate(LocalDate from, LocalDate to) {
		Period period = Period.between(from, to);
		return from
			.plusMonths(random.nextInt((period.getMonths() < 0) ? period.getMonths() * -1 : period.getMonths()))
			.plusDays(random.nextInt((period.getDays() < 0) ? period.getDays() * -1 : period.getDays()));
	}

	/**
	 * 다양한 청구 상태 생성 메서드
	 * @param billingDate 청구일
	 * @return 생성된 청구 상태
	 */
	public BillingStatus getRandomBillingStatus(LocalDate billingDate) {
		LocalDate now = LocalDate.now();
		int randomValue = random.nextInt(100);

		if (now.isAfter(billingDate)) {
			// 청구일이 지난 경우
			if (randomValue < 2) {  // 2% 확률로 미납
				return BillingStatus.NON_PAID;
			} else {  // 98% 확률로 완납
				return BillingStatus.PAID;
			}
		} else if (now.isEqual(billingDate)) {
			// 청구일인 경우
			if (randomValue < 70) {  // 70% 확률로 수납대기
				return BillingStatus.WAITING_PAYMENT;
			} else if (randomValue < 95) {  // 25% 확률로 완납 (조기 납부)
				return BillingStatus.PAID;
			} else {  // 5% 확률로 생성 상태 (아직 처리되지 않음)
				return BillingStatus.CREATED;
			}
		} else {
			// 청구일이 아직 도래하지 않은 경우
			if (randomValue < 80) {  // 80% 확률로 생성 상태
				return BillingStatus.CREATED;
			} else if (randomValue < 95) {  // 15% 확률로 수납대기 (조기 청구서 발송)
				return BillingStatus.WAITING_PAYMENT;
			} else {  // 5% 확률로 완납 (매우 조기 납부)
				return BillingStatus.PAID;
			}
		}
	}

	/**
	 * 랜덤 청구서 메시지 생성 메서드
	 * @return 생성된 랜덤 청구서 메시지
	 */
	public String generateRandomInvoiceMessage() {
		String[] messages = {
			null,
			"수강료 납부에 감사드립니다.",
			"이번 달 수강료 청구서입니다.",
			"문의사항은 학원 사무실로 연락주세요.",
			"수강료 납부 기한을 지켜주세요.",
			"성적 향상을 위해 열심히 지도하겠습니다."
		};
		return messages[random.nextInt(messages.length)];
	}
}